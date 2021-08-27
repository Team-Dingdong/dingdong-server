package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisChatMessage implements Serializable {

    private String roomId;
    private String sender;
    private MessageType type;
    private String message;
}
