package dingdong.dingdong.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.domain.chat.*;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.domain.user.UserRepository;
import dingdong.dingdong.domain.chat.RedisChatMessage;
import dingdong.dingdong.util.exception.ResourceNotFoundException;
import dingdong.dingdong.util.exception.ResultCode;
import java.time.LocalDateTime;
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
            // RedisChatMessage 객채로 맵핑
            RedisChatMessage redisChatMessage = objectMapper
                .readValue(publishMessage, RedisChatMessage.class);

            // 메시지로부터 채팅방 DB 찾기
            ChatRoom chatRoom = chatRoomRepository
                .findByPostId(redisChatMessage.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

            // 메시지로부터 회원 DB 찾기
            User user = userRepository.findById(Long.parseLong(redisChatMessage.getSender()))
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));
            String nickname = user.getProfile().getNickname();
            String profileImageUrl = user.getProfile().getProfileImageUrl();

            // MessageType에 따라 처리
            if (MessageType.ENTER.equals(redisChatMessage.getType())) {
                redisChatMessage.setAdminMessage("띵-동", nickname + "님이 입장하였습니다");
                user = userRepository.getById(1L);
            } else if (MessageType.QUIT.equals(redisChatMessage.getType())) {
                redisChatMessage.setAdminMessage("띵-동", nickname + "님이 퇴장하였습니다");
                user = userRepository.getById(1L);
            } else {
                redisChatMessage.setUserMessage(nickname, profileImageUrl);
            }

            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/topic/chat/room/" + redisChatMessage.getRoomId(),
                redisChatMessage);

            // 메시지 DB에 저장하기 위해 객체 생성
            ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .type(redisChatMessage.getType())
                .message(redisChatMessage.getMessage())
                .sendTime(LocalDateTime.now())
                .build();
            chatMessageRepository.save(chatMessage);

            chatRoom.setInfo(chatMessage);
            chatRoomRepository.save(chatRoom);

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
