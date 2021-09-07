package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUserResponseDto {

    private Long userId;

    private String nickname;

    private String profileImageUrl;

    private boolean isOwner;

    public static ChatRoomUserResponseDto from(ChatRoom chatRoom, User user) {
        return ChatRoomUserResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getProfile().getNickname())
                .profileImageUrl(user.getProfile().getProfileImageUrl())
                .isOwner(chatRoom.getPost().getUser().getId() == user.getId() ? true : false)
                .build();
    }
}
