package dingdong.dingdong.service.chat;

import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.chat.*;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.dto.chatpromise.ChatPromiseResponseDto;
import dingdong.dingdong.util.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final ChatPromiseRepository chatPromiseRepository;
    private final ChatPromiseVoteRepository chatPromiseVoteRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final ChatSubscriber chatSubscriber;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private Long adminId = Long.parseLong("1");

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
        List<ChatRoomResponseDto> data = chatRooms.stream().map(chatRoom -> ChatRoomResponseDto.from(chatRoom, user)).collect(Collectors.toList());
        return data;
    }

    // 채팅방 정보 조회
    @Transactional
    public ChatRoomResponseDto findRoomById(User user, String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        if(!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        return ChatRoomResponseDto.from(chatRoom, user);
    }

    // 채팅방 입장
    @Transactional
    public void enterChatRoom(User user, String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId()).orElse(null);
        if(chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new DuplicateException(ResultCode.CHAT_ROOM_DUPLICATION);
        }
        if(chatPromise != null && chatPromise.getType() != PromiseType.END) {
            throw new LimitException(ResultCode.CHAT_ROOM_ENTER_FAIL_PROMISE);
        }
        if(chatRoom.getPost().getGatheredPeople() >= chatRoom.getPost().getPeople()) {
            throw new LimitException(ResultCode.CHAT_ROOM_ENTER_FAIL_LIMIT);
        }
        ChatJoin chatJoin = new ChatJoin(chatRoom, user);
        chatJoinRepository.save(chatJoin);
        chatRoom.getPost().plusUserCount();

        User admin = userRepository.getById(adminId);

        String message = user.getProfile().getNickname() + ChatMessageValue.ENTER_MESSAGE.getMessage();
        RedisChatMessage redisChatMessage = new RedisChatMessage(chatRoom, admin, MessageType.ENTER, message);
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = new ChatMessage(chatRoom, admin, redisChatMessage);
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        log.info("chatMessage -> {}", chatRoom.getLastChatMessage());
        log.info("chatTime -> {}", chatRoom.getLastChatTime());
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 나가기
    @Transactional
    public void quitChatRoom(User user, String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        if(!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if(chatRoom.getPost().getUser().getId() == user.getId()) {
            throw new ForbiddenException(ResultCode.CHAT_ROOM_QUIT_FAIL_OWNER);
        }
        if(chatRoom.getChatPromise() != null && chatRoom.getChatPromise().getType() != PromiseType.END) {
            throw new LimitException(ResultCode.CHAT_ROOM_QUIT_FAIL);
        }
        ChatJoin chatJoin = chatJoinRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_JOIN_NOT_FOUND));
        chatJoinRepository.delete(chatJoin);
        chatRoom.getPost().minusUserCount();

        User admin = userRepository.getById(adminId);

        String message = user.getProfile().getNickname() + ChatMessageValue.QUIT_MESSAGE.getMessage();
        RedisChatMessage redisChatMessage = new RedisChatMessage(chatRoom, admin, MessageType.QUIT, message);
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = new ChatMessage(chatRoom, admin, redisChatMessage);
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        log.info("chatMessage -> {}", chatRoom.getLastChatMessage());
        log.info("chatTime -> {}", chatRoom.getLastChatTime());
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 사용자 목록 조회
    @Transactional
    public List<ChatRoomUserResponseDto> findUsers(User user, String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        if(!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        List<ChatJoin> chatJoins = chatJoinRepository.findAllByChatRoom(chatRoom);
        List<User> users = chatJoins.stream().map(ChatJoin::getUser).collect(Collectors.toList());
        users.stream().forEach(u -> log.info("user : {}", u.getId()));
        List<ChatRoomUserResponseDto> data = users.stream().map(u -> ChatRoomUserResponseDto.from(chatRoom, u)).collect(Collectors.toList());
        return data;
    }

    // 채팅 메세지 조회
    @Transactional
    public List<ChatMessageResponseDto> findChatMessages(User user, String id) {
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        if(!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        List<ChatMessage> messages = chatRoom.getMessages();
        List<ChatMessageResponseDto> data = messages.stream().map(ChatMessageResponseDto::from).collect(Collectors.toList());
        return data;
    }

    // 채팅 약속 조회
    @Transactional
    public ChatPromiseResponseDto findByPostId(User user, String id){
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        if(!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId()).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_PROMISE_NOT_FOUND));
        ChatPromiseResponseDto promiseResponseDto = new ChatPromiseResponseDto(chatPromise);
        return promiseResponseDto;
    }

    // 채팅 약속 수정
    @Transactional
    public void updatePromise(User user, String id, ChatPromiseRequestDto request){
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId()).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_PROMISE_NOT_FOUND));

        if(chatRoom.getPost().getUser().getId() != user.getId()) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }

        if(chatPromise.getType() == PromiseType.CONFIRMED) {
            throw new LimitException(ResultCode.CHAT_PROMISE_UPDATE_FAIL_CONFIRMED);
        }

        if(request.getPromiseDate() != null) {
            chatPromise.setPromiseDate(request.getPromiseDate());
        }

        if(request.getPromiseTime() != null ) {
            chatPromise.setPromiseTime(request.getPromiseTime());
        }

        if(request.getPromiseLocal() != null) {
            chatPromise.setPromiseLocal(request.getPromiseLocal());
        }

        List<ChatPromiseVote> chatPromiseVotes = chatPromiseVoteRepository.findAllByChatRoom(chatRoom);
        chatPromiseVoteRepository.deleteAll(chatPromiseVotes);

        ChatPromiseVote chatPromiseVote = new ChatPromiseVote(chatRoom, user);
        chatPromiseVoteRepository.save(chatPromiseVote);

        chatPromise.updateAll();
        chatPromiseRepository.save(chatPromise);

        User admin = userRepository.getById(adminId);

        String message = "[나눔 약속 수정] " + chatPromise.getPromiseDate().toString() + " " + chatPromise.getPromiseTime().toString() + " " + chatPromise.getPromiseLocal() + ChatMessageValue.PROMISE_UPDATE_MESSAGE.getMessage();
        RedisChatMessage redisChatMessage = new RedisChatMessage(chatRoom, admin, MessageType.PROMISE_AGAIN, message);
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = new ChatMessage(chatRoom, admin, redisChatMessage);
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        log.info("chatMessage -> {}", chatRoom.getLastChatMessage());
        log.info("chatTime -> {}", chatRoom.getLastChatTime());
        chatRoomRepository.save(chatRoom);
    }


    // 채팅 약속 생성
    @Transactional
    public void createChatPromise(User user, String id, ChatPromiseRequestDto request){
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        if(chatRoom.getPost().getUser().getId() != user.getId()) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        
        if(chatPromiseRepository.existsByChatRoomId(chatRoom.getId())) {
            throw new DuplicateException(ResultCode.CHAT_PROMISE_DUPLICATION);
        }

        if(chatRoom.getPost().getGatheredPeople() == 1) {
            throw new LimitException(ResultCode.CHAT_PROMISE_CREATE_FAIL_ONLY);
        }

        ChatPromise chatPromise = new ChatPromise(chatRoom, request);
        ChatPromiseVote chatPromiseVote = new ChatPromiseVote(chatRoom, user);

        chatPromiseRepository.save(chatPromise);
        chatPromiseVoteRepository.save(chatPromiseVote);

        User admin = userRepository.getById(adminId);

        String message = "[나눔 약속] " + request.getPromiseDate().toString() + " " + request.getPromiseTime().toString() + " " + request.getPromiseLocal() + ChatMessageValue.PROMISE_CREATE_MESSAGE.getMessage();
        RedisChatMessage redisChatMessage = new RedisChatMessage(chatRoom, admin, MessageType.PROMISE, message);
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = new ChatMessage(chatRoom, admin, redisChatMessage);
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        log.info("chatMessage -> {}", chatRoom.getLastChatMessage());
        log.info("chatTime -> {}", chatRoom.getLastChatTime());
        chatRoomRepository.save(chatRoom);
    }

    // 일정시간마다 Scheduling 작동.
    @Scheduled(fixedDelay=60000 * 60) // 1시간마다 작동
    public void checkEndTime() {
        chatPromiseRepository.updateByLocalDateTime();
    }

    // 채팅 약속 투표
    @Transactional
    public void createVotePromise(User user, String id){
        RedisChatRoom redisChatRoom = redisChatRoomRepository.findById(id);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId()).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_PROMISE_NOT_FOUND));
        if(!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if(chatPromise.getType() != PromiseType.PROGRESS) {
            throw new LimitException(ResultCode.CHAT_PROMISE_NOT_IN_PRGRESS);
        }
        if(!chatPromiseVoteRepository.existsByChatRoomAndUser(chatRoom, user)){
            ChatPromiseVote chatPromiseVote = new ChatPromiseVote(chatRoom, user);
            chatPromiseVoteRepository.save(chatPromiseVote);
            chatPromise.plusVotingPeople();
            chatPromiseRepository.save(chatPromise);

            if(chatPromise.getType() == PromiseType.CONFIRMED) {
                User admin = userRepository.getById(adminId);

                String message = "[나눔 약속 확정] " + chatPromise.getPromiseDate().toString() + " " + chatPromise.getPromiseTime().toString() + " " + chatPromise.getPromiseLocal() + ChatMessageValue.PROMISE_CONFIRMED_MESSAGE.getMessage();
                RedisChatMessage redisChatMessage = new RedisChatMessage(chatRoom, admin, MessageType.PROMISE_CONFIRMED, message);
                chatSubscriber.sendMessage(redisChatMessage);

                // 메시지 DB에 저장하기 위해 객체 생성
                ChatMessage chatMessage = new ChatMessage(chatRoom, admin, redisChatMessage);
                chatMessageRepository.save(chatMessage);

                chatRoom.setInfo(chatMessage);
                log.info("chatMessage -> {}", chatRoom.getLastChatMessage());
                log.info("chatTime -> {}", chatRoom.getLastChatTime());
                chatRoomRepository.save(chatRoom);
            }
        } else {
            throw new DuplicateException(ResultCode.CHAT_PROMISE_VOTE_DUPLICATION);
        }
    }

}