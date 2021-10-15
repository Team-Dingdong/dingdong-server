package dingdong.dingdong.service.chat;

import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.chat.*;
import dingdong.dingdong.util.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatJoinRepository chatJoinRepository;
    private final ChatPromiseRepository chatPromiseRepository;
    private final ChatPromiseVoteRepository chatPromiseVoteRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSubscriber chatSubscriber;

    private final UserRepository userRepository;
    private static final Long ADMINID = 1L;

    // 채팅방 생성
    @Transactional
    public void createChatRoom(Post post) {
        ChatRoom chatRoom = ChatRoom.builder()
            .id(post.getId())
            .post(post)
            .build();
        ChatJoin chatJoin = ChatJoin.builder()
            .chatRoom(chatRoom)
            .user(post.getUser())
            .build();

        chatRoomRepository.save(chatRoom);
        chatJoinRepository.save(chatJoin);

        chatRoom.getPost().plusUserCount();
    }

    // 채팅방 목록 조회
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> findAllRoom(User user) {
        List<ChatJoin> chatJoins = chatJoinRepository.findAllByUser(user);
        List<ChatRoom> chatRooms = chatJoins.stream().map(ChatJoin::getChatRoom)
            .collect(Collectors.toList());

        return chatRooms.stream().map(chatRoom -> ChatRoomResponseDto.from(chatRoom, user))
            .collect(Collectors.toList());
    }

    // 채팅방 정보 조회
    @Transactional(readOnly = true)
    public ChatRoomResponseDto findRoomById(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        if (!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }

        return ChatRoomResponseDto.from(chatRoom, user);
    }

    // 채팅방 입장
    @Transactional
    public void enterChatRoom(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId())
            .orElse(null);

        if (chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new DuplicateException(ResultCode.CHAT_ROOM_DUPLICATION);
        }
        if (chatRoom.getPost().getDone()) {
            throw new LimitException(ResultCode.CHAT_ROOM_ENTER_FAIL_DONE);
        }
        if (chatPromise != null && chatPromise.getType() != PromiseType.END) {
            throw new LimitException(ResultCode.CHAT_ROOM_ENTER_FAIL_PROMISE);
        }
        if (chatRoom.getPost().getGatheredPeople() >= chatRoom.getPost().getPeople()) {
            throw new LimitException(ResultCode.CHAT_ROOM_ENTER_FAIL_LIMIT);
        }

        ChatJoin chatJoin = ChatJoin.builder()
            .chatRoom(chatRoom)
            .user(user)
            .build();
        chatJoinRepository.save(chatJoin);

        chatRoom.getPost().plusUserCount();

        User admin = userRepository.getById(ADMINID);

        String message = user.getProfile().getNickname() + ChatMessageValue.ENTER_MESSAGE.getMessage();

        RedisChatMessage redisChatMessage = RedisChatMessage.builder()
            .roomId(chatRoom.getId())
            .sender(admin.getId().toString())
            .profileImageUrl(admin.getProfile().getProfileImageUrl())
            .type(MessageType.ENTER)
            .message(message)
            .build();
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(admin)
            .type(redisChatMessage.getType())
            .message(redisChatMessage.getMessage())
            .sendTime(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 나가기
    @Transactional
    public void quitChatRoom(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        if (!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if (chatRoom.getPost().getUser().getId() == user.getId()) {
            throw new ForbiddenException(ResultCode.CHAT_ROOM_QUIT_FAIL_OWNER);
        }
        if (chatRoom.getChatPromise() != null
            && chatRoom.getChatPromise().getType() != PromiseType.END) {
            throw new LimitException(ResultCode.CHAT_ROOM_QUIT_FAIL);
        }

        ChatJoin chatJoin = chatJoinRepository.findByChatRoomAndUser(chatRoom, user)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_JOIN_NOT_FOUND));
        chatJoinRepository.delete(chatJoin);

        chatRoom.getPost().minusUserCount();

        User admin = userRepository.getById(ADMINID);

        String message =
            user.getProfile().getNickname() + ChatMessageValue.QUIT_MESSAGE.getMessage();
        RedisChatMessage redisChatMessage = RedisChatMessage.builder()
            .roomId(chatRoom.getId())
            .sender(admin.getId().toString())
            .profileImageUrl(admin.getProfile().getProfileImageUrl())
            .type(MessageType.QUIT)
            .message(message)
            .build();
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(admin)
            .type(redisChatMessage.getType())
            .message(redisChatMessage.getMessage())
            .sendTime(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 사용자 목록 조회
    @Transactional(readOnly = true)
    public List<ChatRoomUserResponseDto> findUsers(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        if (!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }

        List<ChatJoin> chatJoins = chatJoinRepository.findAllByChatRoom(chatRoom);
        List<User> users = chatJoins.stream().map(ChatJoin::getUser).collect(Collectors.toList());

        return users.stream().map(u -> ChatRoomUserResponseDto.from(chatRoom, u))
            .collect(Collectors.toList());
    }

    // 채팅 메세지 조회
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> findChatMessages(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        if (!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }

        List<ChatMessage> messages = chatRoom.getMessages();

        return messages.stream().map(ChatMessageResponseDto::from).collect(Collectors.toList());
    }

    // 채팅 약속 조회
    @Transactional(readOnly = true)
    public ChatPromiseResponseDto findByPostId(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        if (!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }

        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_PROMISE_NOT_FOUND));

        return ChatPromiseResponseDto.from(chatPromise);
    }

    // 채팅 약속 수정
    @Transactional
    public void updatePromise(User user, Long id, ChatPromiseRequestDto chatPromiseRequestDto) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_PROMISE_NOT_FOUND));

        if (chatRoom.getPost().getUser().getId() != user.getId()) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if (chatPromise.getType() == PromiseType.CONFIRMED) {
            throw new LimitException(ResultCode.CHAT_PROMISE_UPDATE_FAIL_CONFIRMED);
        }
        if (chatPromiseRequestDto.getPromiseDate() != null) {
            chatPromise.setPromiseDate(chatPromiseRequestDto.getPromiseDate());
        }
        if (chatPromiseRequestDto.getPromiseTime() != null) {
            chatPromise.setPromiseTime(chatPromiseRequestDto.getPromiseTime());
        }
        if (chatPromiseRequestDto.getPromiseLocal() != null) {
            chatPromise.setPromiseLocal(chatPromiseRequestDto.getPromiseLocal());
        }

        List<ChatPromiseVote> chatPromiseVotes = chatPromiseVoteRepository
            .findAllByChatRoom(chatRoom);
        chatPromiseVoteRepository.deleteAll(chatPromiseVotes);

        ChatPromiseVote chatPromiseVote = ChatPromiseVote.builder()
            .chatRoom(chatRoom)
            .user(user)
            .build();
        chatPromiseVoteRepository.save(chatPromiseVote);

        chatPromise.updateAll();
        chatPromiseRepository.save(chatPromise);

        User admin = userRepository.getById(ADMINID);

        String message = "[나눔 약속 수정] " + chatPromise.getPromiseDate().toString() + " " + chatPromise
            .getPromiseTime().toString() + " " + chatPromise.getPromiseLocal()
            + ChatMessageValue.PROMISE_UPDATE_MESSAGE.getMessage();
        RedisChatMessage redisChatMessage = RedisChatMessage.builder()
            .roomId(chatRoom.getId())
            .sender(admin.getId().toString())
            .profileImageUrl(admin.getProfile().getProfileImageUrl())
            .type(MessageType.PROMISE_AGAIN)
            .message(message)
            .build();
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(admin)
            .type(redisChatMessage.getType())
            .message(redisChatMessage.getMessage())
            .sendTime(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        chatRoomRepository.save(chatRoom);
    }


    // 채팅 약속 생성
    @Transactional
    public void createChatPromise(User user, Long id, ChatPromiseRequestDto chatPromiseRequestDto) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

        if (chatRoom.getPost().getUser().getId() != user.getId()) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if (chatPromiseRepository.existsByChatRoomId(chatRoom.getId())) {
            throw new DuplicateException(ResultCode.CHAT_PROMISE_DUPLICATION);
        }
        if (chatRoom.getPost().getGatheredPeople() == 1) {
            throw new LimitException(ResultCode.CHAT_PROMISE_CREATE_FAIL_ONLY);
        }

        ChatPromise chatPromise = ChatPromise.builder()
            .id(chatRoom.getId())
            .chatRoom(chatRoom)
            .promiseDate(chatPromiseRequestDto.getPromiseDate())
            .promiseTime(chatPromiseRequestDto.getPromiseTime())
            .promiseLocal(chatPromiseRequestDto.getPromiseLocal())
            .type(PromiseType.PROGRESS)
            .promiseEndTime(LocalDateTime.now().plusHours(3))
            .totalPeople(chatRoom.getPost().getGatheredPeople())
            .votingPeople(1)
            .build();
        ChatPromiseVote chatPromiseVote = ChatPromiseVote.builder()
            .chatRoom(chatRoom)
            .user(user)
            .build();

        chatPromiseRepository.save(chatPromise);
        chatPromiseVoteRepository.save(chatPromiseVote);

        User admin = userRepository.getById(ADMINID);

        String message =
            "[나눔 약속] " + chatPromiseRequestDto.getPromiseDate().toString() + " " + chatPromiseRequestDto.getPromiseTime()
                .toString() + " " + chatPromiseRequestDto.getPromiseLocal()
                + ChatMessageValue.PROMISE_CREATE_MESSAGE.getMessage();
        RedisChatMessage redisChatMessage = RedisChatMessage.builder()
            .roomId(chatRoom.getId())
            .sender(admin.getId().toString())
            .profileImageUrl(admin.getProfile().getProfileImageUrl())
            .type(MessageType.PROMISE)
            .message(message)
            .build();
        chatSubscriber.sendMessage(redisChatMessage);

        // 메시지 DB에 저장하기 위해 객체 생성
        ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(admin)
            .type(redisChatMessage.getType())
            .message(redisChatMessage.getMessage())
            .sendTime(LocalDateTime.now())
            .build();
        chatMessageRepository.save(chatMessage);

        chatRoom.setInfo(chatMessage);
        chatRoomRepository.save(chatRoom);
    }

    // 채팅 약속 투표
    @Transactional
    public void createVotePromise(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_PROMISE_NOT_FOUND));

        if (!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if (chatPromise.getType() != PromiseType.PROGRESS) {
            throw new LimitException(ResultCode.CHAT_PROMISE_NOT_IN_PRGRESS);
        }

        if (!chatPromiseVoteRepository.existsByChatRoomAndUser(chatRoom, user)) {
            ChatPromiseVote chatPromiseVote = ChatPromiseVote.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();
            chatPromiseVoteRepository.save(chatPromiseVote);
            chatPromise.plusVotingPeople();
            chatPromiseRepository.save(chatPromise);

            if (chatPromise.getType() == PromiseType.CONFIRMED) {
                User admin = userRepository.getById(ADMINID);

                String message =
                    "[나눔 약속 확정] " + chatPromise.getPromiseDate().toString() + " " + chatPromise
                        .getPromiseTime().toString() + " " + chatPromise.getPromiseLocal()
                        + ChatMessageValue.PROMISE_CONFIRMED_MESSAGE.getMessage();
                RedisChatMessage redisChatMessage = RedisChatMessage.builder()
                    .roomId(chatRoom.getId())
                    .sender(admin.getId().toString())
                    .profileImageUrl(admin.getProfile().getProfileImageUrl())
                    .type(MessageType.PROMISE_CONFIRMED)
                    .message(message)
                    .build();
                chatSubscriber.sendMessage(redisChatMessage);

                // 메시지 DB에 저장하기 위해 객체 생성
                ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(admin)
                    .type(redisChatMessage.getType())
                    .message(redisChatMessage.getMessage())
                    .sendTime(LocalDateTime.now())
                    .build();
                chatMessageRepository.save(chatMessage);

                chatRoom.setInfo(chatMessage);
                chatRoomRepository.save(chatRoom);
            }
        } else {
            throw new DuplicateException(ResultCode.CHAT_PROMISE_VOTE_DUPLICATION);
        }
    }

    // 나누기 거래 확정
    @Transactional
    public void confirmedPost(User user, Long id) {
        ChatRoom chatRoom = chatRoomRepository.findByPostId(id)
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        ChatPromise chatPromise = chatPromiseRepository.findByChatRoomId(chatRoom.getId())
            .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_PROMISE_NOT_FOUND));

        if (!chatJoinRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if (chatRoom.getPost().getUser().getId() != user.getId()) {
            throw new ForbiddenException(ResultCode.FORBIDDEN_MEMBER);
        }
        if (chatRoom.getPost().getDone()) {
            throw new DuplicateException(ResultCode.POST_CONFIRMED_DUPLICATION);
        }
        if (chatPromise.getType() != PromiseType.CONFIRMED) {
            throw new LimitException(ResultCode.POST_CONFIRMED_FAIL_PROMISE);
        }
        if (LocalDateTime.of(chatPromise.getPromiseDate(), chatPromise.getPromiseTime())
            .compareTo(LocalDateTime.now()) > 0) {
            throw new LimitException(ResultCode.POST_CONFIRMED_FAIL_TIME);
        }

        chatRoom.getPost().confirmed();

    }

    // 일정시간마다 Scheduling 작동.
    @Transactional
    @Scheduled(fixedDelay = 60000 * 60) // 1시간마다 작동
    public void checkEndTime() {
        chatPromiseRepository.updateByLocalDateTime();
    }
}