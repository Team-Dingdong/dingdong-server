package dingdong.dingdong.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.dto.chat.RedisChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            log.info("publishMessage : {}", publishMessage);
            // ChatMessage 객채로 맵핑
            RedisChatMessage redisChatMessage = objectMapper.readValue(publishMessage, RedisChatMessage.class);
            log.info("redisChatMessage : {}", redisChatMessage);
            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + redisChatMessage.getRoomId(), redisChatMessage);
            //  + redisChatMessage.getRoomId()
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
