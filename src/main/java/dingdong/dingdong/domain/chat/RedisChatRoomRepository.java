package dingdong.dingdong.domain.chat;

import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepository {

    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, RedisChatRoom> redisTemplate;
    private HashOperations<String, String, RedisChatRoom> opsHashChatRoom;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    public void save(RedisChatRoom redisChatRoom) {
        opsHashChatRoom.put(CHAT_ROOMS, redisChatRoom.getRoomId(), redisChatRoom);
    }

    public RedisChatRoom findById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    public Map<String, RedisChatRoom> findAll() {
        return opsHashChatRoom.entries(CHAT_ROOMS);
    }

    public void update(RedisChatRoom redisChatRoom) {
        save(redisChatRoom);
    }

    public void delete(String id) {
        opsHashChatRoom.delete(CHAT_ROOMS, id);
    }
}
