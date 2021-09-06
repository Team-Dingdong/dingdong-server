package dingdong.dingdong.service.chat;

import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.*;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.util.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static dingdong.dingdong.util.exception.ResultCode.CHAT_PROMISE_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor

@Service
public class ChatService {

    private final ChatJoinRepository chatJoinRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatPromiseRepository chatPromiseRepository;
    private final ChatPromiseVoteRepository chatPromiseVoteRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;

    // 채팅방 생성
    @Transactional
    public void createChatRoom(Post post) {
        ChatRoom chatRoom = new ChatRoom(post);
        RedisChatRoom redisChatRoom = new RedisChatRoom(chatRoom);
        ChatJoin chatJoin = new ChatJoin(chatRoom, post.getUser());
        chatRoomRepository.save(chatRoom);
        redisChatRoomRepository.save(redisChatRoom);
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
        if(chatRoom.getPost().getUser().getId() == user.getId()) {
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

    // 채팅 약속 조회
    public ChatPromiseResponseDto findByPostId(String id){
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoom(chatRoom).orElseThrow(() -> new ResourceNotFoundException(CHAT_PROMISE_NOT_FOUND));
        ChatPromiseResponseDto promiseResponseDto = new ChatPromiseResponseDto(chatPromise);
        return promiseResponseDto;
    }

    // 채팅 약속 수정
    public void updatePromise(User user, String id, ChatPromiseRequestDto request){
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoom(chatRoom).orElseThrow(() -> new ResourceNotFoundException(CHAT_PROMISE_NOT_FOUND));

        // LocalDate과 LocalTime을 합쳐 LocalDateTime으로 변환
        LocalDate date = request.getPromiseDate();
        LocalTime time = request.getPromiseTime();
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        chatPromise.setPromiseDateTime(dateTime);
        chatPromise.setPromiseEndTime(dateTime.plusHours(3));
        chatPromise.setType(PromiseType.PROGRESS);

        chatPromiseRepository.save(chatPromise);
    }

    // 채팅 약속 생성
    public void createChatPromise(User user, String id, ChatPromiseRequestDto request){
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        ChatPromise chatPromise = new ChatPromise(chatRoom);
        ChatPromiseVote chatPromiseVote = new ChatPromiseVote(chatRoom, user);

        LocalDate date = request.getPromiseDate();
        LocalTime time = request.getPromiseTime();
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        chatPromise.setPromiseDateTime(dateTime);
        chatPromise.setPromiseEndTime(dateTime.plusHours(3));
        chatPromise.setType(PromiseType.PROGRESS);

        chatPromiseRepository.save(chatPromise);
        chatPromiseVoteRepository.save(chatPromiseVote);
    }

    // 일정시간마다 Scheduling 작동.
    @Scheduled(fixedDelay=60000 * 60) // 1시간마다 작동
    public void checkEndTime() {
        chatPromiseRepository.updateByLocalDateTime();
    }

}