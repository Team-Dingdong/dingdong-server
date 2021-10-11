package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chat.RedisChatMessage;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

    private MessageType type;
    private String message;
    private LocalDateTime sendTime;

    public ChatMessage(ChatRoom chatRoom, User user, RedisChatMessage redisChatMessage) {
        this.chatRoom = chatRoom;
        this.sender = user;
        this.type = redisChatMessage.getType();
        this.message = redisChatMessage.getMessage();
        this.sendTime = LocalDateTime.now();
    }
}
