package dingdong.dingdong.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.dto.chat.RedisChatMessage;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
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
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

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

            // 메시지로부터 채팅방 DB 찾기
            ChatRoom chatRoom = chatRoomRepository.findByPostId(Long.parseLong(redisChatMessage.getRoomId())).orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

            // 메시지로부터 회원 DB 찾기
            User user = userRepository.findByPhone(redisChatMessage.getSender());
            if(user == null) {
                throw new ResourceNotFoundException(ResultCode.USER_NOT_FOUND);
            }

            if(MessageType.ENTER.equals(redisChatMessage.getType())) {

            }
            // 메시지 DB에 저장하기 위해 객체 생성
            ChatMessage chatMessage = new ChatMessage(chatRoom, user, redisChatMessage);
            chatMessageRepository.save(chatMessage);

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
