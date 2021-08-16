package dingdong.dingdong.controller;

import dingdong.dingdong.domain.chat.ChatMessage;
import dingdong.dingdong.domain.chat.MessageType;
import dingdong.dingdong.service.chat.ChatService;
import dingdong.dingdong.service.chat.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatService chatService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (MessageType.ENTER.equals(message.getType())) {
            chatService.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(chatService.getTopic(message.getRoomId()), message);
    }
}
