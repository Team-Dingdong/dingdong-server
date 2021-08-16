package dingdong.dingdong.domain.chat;

import lombok.Data;

@Data
public class ChatMessage {

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
}
