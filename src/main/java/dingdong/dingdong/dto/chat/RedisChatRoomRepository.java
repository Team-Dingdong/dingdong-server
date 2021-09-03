package dingdong.dingdong.dto.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepository {

    private static final String CHAT_ROOMS = "chatroom";
    private final RedisTemplate<String, RedisChatRoom> redisTemplate;
    private HashOperations<String, String, RedisChatRoom> opsHashChatRoom;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    public void save(RedisChatRoom redisChatRoom) {
        opsHashChatRoom.put(CHAT_ROOMS, redisChatRoom.getRoomId(), redisChatRoom);
    }

    public Map<String, RedisChatRoom> findAll() {
        return opsHashChatRoom.entries(CHAT_ROOMS);
    }

    public RedisChatRoom findById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    public void update(RedisChatRoom redisChatRoom) {
        save(redisChatRoom);
    }

    public void delete(String id) {
        opsHashChatRoom.delete(CHAT_ROOMS, id);
    }
}
