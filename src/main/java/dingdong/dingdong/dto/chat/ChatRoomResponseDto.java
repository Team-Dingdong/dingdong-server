package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.user.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {

    private Long id;

    private String title;

    private String lastChatMessage;

    private LocalDateTime lastChatTime;

    private String imageUrl;

    private Integer userCount;

    private Boolean isOwner;

    public static ChatRoomResponseDto from(ChatRoom chatRoom, User user) {
        return ChatRoomResponseDto.builder()
            .id(chatRoom.getId())
            .title(chatRoom.getPost().getTitle())
            .lastChatMessage(chatRoom.getLastChatMessage())
            .lastChatTime(chatRoom.getLastChatTime())
            .imageUrl(chatRoom.getPost().getImageUrl1())
            .userCount(chatRoom.getPost().getGatheredPeople())
            .isOwner(chatRoom.getPost().getUser().getId() == user.getId())
            .build();
    }
}
