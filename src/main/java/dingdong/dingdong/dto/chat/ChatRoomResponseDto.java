package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatRoom;
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

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getPost().getTitle())
                .userCount(chatRoom.getUserCount())
                .lastChatMessage(chatRoom.getLastChatMessage())
                .lastChatTime(chatRoom.getLastChatTime())
                .imageUrl(chatRoom.getPost().getImageUrl1())
                .build();
    }
}
