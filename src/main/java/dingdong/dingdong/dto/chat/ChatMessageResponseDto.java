package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatMessage;
import dingdong.dingdong.domain.chat.MessageType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    private Boolean isOwner;

    private MessageType type;

    private String message;

    private LocalDateTime sendTime;

    public static ChatMessageResponseDto from(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
            .userId(chatMessage.getSender().getId())
            .nickname(chatMessage.getSender().getProfile().getNickname())
            .profileImageUrl(chatMessage.getSender().getProfile().getProfileImageUrl())
            .isOwner(
                chatMessage.getChatRoom().getPost().getUser().getId() == chatMessage.getSender()
                    .getId())
            .type(chatMessage.getType())
            .message(chatMessage.getMessage())
            .sendTime(chatMessage.getSendTime())
            .build();
    }
}
