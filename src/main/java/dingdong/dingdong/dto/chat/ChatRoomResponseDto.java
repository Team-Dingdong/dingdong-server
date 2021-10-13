package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponseDto {

    private Long id;

    private String title;

    private String lastChatMessage;

    private LocalDateTime lastChatTime;

    private String imageUrl;

    private int userCount;

    private boolean isOwner;

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
