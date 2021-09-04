package dingdong.dingdong.service.chat;

import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.*;
import dingdong.dingdong.util.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatJoinRepository chatJoinRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;

    // 채팅방 생성
    @Transactional
    public void createChatRoom(Post post) {
        ChatRoom chatRoom = new ChatRoom(post);
        RedisChatRoom redisChatRoom = new RedisChatRoom(chatRoom);
        ChatJoin chatJoin = new ChatJoin(chatRoom, post.getUser());
        redisChatRoomRepository.save(redisChatRoom);
        chatRoomRepository.save(chatRoom);
        chatJoinRepository.save(chatJoin);
        chatRoom.getPost().plusUserCount();
    }

    // 채팅방 목록 조회
    @Transactional
    public List<ChatRoomResponseDto> findAllRoom(User user) {
        List<ChatJoin> chatJoins = chatJoinRepository.findAllByUser(user);
        List<ChatRoom> chatRooms = chatJoins.stream().map(ChatJoin::getChatRoom).collect(Collectors.toList());
        List<ChatRoomResponseDto> data = chatRooms.stream().map(ChatRoomResponseDto::from).collect(Collectors.toList());
        return data;
    }

    // 채팅방 정보 조회
    @Transactional
    public ChatRoomResponseDto findRoomById(String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        return ChatRoomResponseDto.from(chatRoom);
    }

    // 채팅방 입장
    @Transactional
    public void enterChatRoom(String id, User user) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        if(chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new DuplicateException(ResultCode.CHAT_ROOM_DUPLICATION);
        }
        if(chatRoom.getPost().getGatheredPeople() >= chatRoom.getPost().getPeople()) {
            throw new LimitException(ResultCode.CHAT_ROOM_ENTER_FAIL_LIMIT);
        }
        ChatJoin chatJoin = new ChatJoin(chatRoom, user);
        chatJoinRepository.save(chatJoin);
        chatRoom.getPost().plusUserCount();
    }

    // 채팅방 나가기
    @Transactional
    public void quitChatRoom(String id, User user) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        if(chatRoom.getPost().getUser() == user) {
            throw new ForbiddenException(ResultCode.CHAT_ROOM_QUIT_FAIL_OWNER);
        }
        ChatJoin chatJoin = chatJoinRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_JOIN_NOT_FOUND));
        chatJoinRepository.delete(chatJoin);
        chatRoom.getPost().minusUserCount();
    }

    // 채팅방 사용자 목록 조회
    @Transactional
    public List<ChatRoomUserResponseDto> findUsers(String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        List<ChatJoin> chatJoins = chatJoinRepository.findAllByChatRoom(chatRoom);
        List<User> users = chatJoins.stream().map(ChatJoin::getUser).collect(Collectors.toList());
        List<ChatRoomUserResponseDto> data = users.stream().map(user -> ChatRoomUserResponseDto.from(chatRoom, user)).collect(Collectors.toList());
        return data;
    }

    // 채팅 메세지 조회
    @Transactional
    public List<ChatMessageResponseDto> findChatMessages(String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        List<ChatMessage> messages = chatRoom.getMessages();
        List<ChatMessageResponseDto> data = messages.stream().map(ChatMessageResponseDto::from).collect(Collectors.toList());
        return data;
    }
}