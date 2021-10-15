package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUserResponseDto {

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    private Boolean isOwner;

    public static ChatRoomUserResponseDto from(ChatRoom chatRoom, User user) {
        return ChatRoomUserResponseDto.builder()
            .userId(user.getId())
            .nickname(user.getProfile().getNickname())
            .profileImageUrl(user.getProfile().getProfileImageUrl())
            .isOwner(chatRoom.getPost().getUser().getId() == user.getId())
            .build();
    }
}
