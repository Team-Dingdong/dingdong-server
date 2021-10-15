package dingdong.dingdong.domain.chat;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisChatMessage implements Serializable {

    private Long roomId;
    private String sender;
    private String profileImageUrl;
    private MessageType type;
    private String message;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setAdminMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public void setUserMessage(String sender, String profileImageUrl) {
        this.sender = sender;
        this.profileImageUrl = profileImageUrl;
    }
}
