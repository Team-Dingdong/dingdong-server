package dingdong.dingdong.domain.chat;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id @Column(name = "chat_id", nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    public ChatRoom(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}