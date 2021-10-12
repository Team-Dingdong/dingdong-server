package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatRoom;
import dingdong.dingdong.domain.chat.MessageType;
import dingdong.dingdong.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisChatMessage implements Serializable {

    private Long roomId;
    private String sender;
    private String profileImageUrl;
    private MessageType type;
    private String message;

    public RedisChatMessage(ChatRoom chatRoom, User user, MessageType type, String message) {
        this.roomId = chatRoom.getId();
        this.sender = user.getId().toString();
        this.profileImageUrl = user.getProfile().getProfileImageUrl();
        this.type = type;
        this.message = message;
    }
}
