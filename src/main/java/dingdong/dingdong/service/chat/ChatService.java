package dingdong.dingdong.service.chat;

import dingdong.dingdong.domain.chat.ChatJoin;
import dingdong.dingdong.domain.chat.ChatJoinRepository;
import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.chat.ChatRoomRepository;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.ChatRoomResponseDto;
import dingdong.dingdong.dto.chat.RedisChatRoom;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatJoinRepository chatJoinRepository;
    private final ChatRoomRepository chatRoomRepository;

    // Redis
    private static final String CHAT_ROOMS = "chatroom";
    private final RedisTemplate<String, RedisChatRoom> redisTemplate;
    private HashOperations<String, String, RedisChatRoom> opsHashChatRoom;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    public List<ChatRoomResponseDto> findAllRoom(User user) {
        log.info("opsHashChatRoom : {}", opsHashChatRoom.keys(CHAT_ROOMS));
        List<ChatJoin> chatJoins = chatJoinRepository.findAllByUser(user);
        List<ChatRoom> chatRooms = chatJoins.stream().map(ChatJoin::getChatRoom).collect(Collectors.toList());
        List<ChatRoomResponseDto> data = chatRooms.stream().map(ChatRoomResponseDto::from).collect(Collectors.toList());
        return data;
    }

    @Transactional
    public ChatRoomResponseDto findRoomById(String id) {
        RedisChatRoom redisChatRoom = opsHashChatRoom.get(CHAT_ROOMS, id);
        log.info("redisChatRoom : {}", redisChatRoom);
        ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatRoom.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));
        return ChatRoomResponseDto.from(chatRoom);
    }

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
    @Transactional
    public void createChatRoom(Post post) {
        ChatRoom chatRoom = new ChatRoom(post);
        RedisChatRoom redisChatRoom = new RedisChatRoom(chatRoom);
        opsHashChatRoom.put(CHAT_ROOMS, redisChatRoom.getRoomId(), redisChatRoom);
        chatRoomRepository.save(chatRoom);
    }
}