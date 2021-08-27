package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisChatRoom implements Serializable {

    private String
            roomId;
    private String title;

    public RedisChatRoom(ChatRoom chatRoom) {
        this.roomId = chatRoom.getId().toString();
        this.title = chatRoom.getPost().getTitle();
    }
}
