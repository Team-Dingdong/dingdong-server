package dingdong.dingdong.service.chat;

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
public class ChatSubscriber {

    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public void sendMessage(RedisChatMessage redisChatMessage) {
        try {
            // 메시지로부터 채팅방 DB 찾기
            ChatRoom chatRoom = chatRoomRepository
                .findByPostId(Long.parseLong(redisChatMessage.getRoomId()))
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.CHAT_ROOM_NOT_FOUND));

            // 메시지로부터 회원 DB 찾기
            User user = userRepository.findById(Long.parseLong(redisChatMessage.getSender()))
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.USER_NOT_FOUND));
            String nickname = user.getProfile().getNickname();
            String profileImageUrl = user.getProfile().getProfileImageUrl();

            redisChatMessage.setSender(nickname);
            redisChatMessage.setProfileImageUrl(profileImageUrl);

            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/topic/chat/room/" + redisChatMessage.getRoomId(),
                redisChatMessage);

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
