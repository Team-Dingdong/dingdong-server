package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.post.Post;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class ChatRoom {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime endDate;

    private String lastChatMessage;

    private LocalDateTime lastChatTime;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToOne(mappedBy = "chatRoom")
    private ChatPromise chatPromise;

    public ChatRoom(Post post) {
        this.id = post.getId();
        this.post = post;
    }

    public void setInfo(ChatMessage chatMessage) {
        this.lastChatMessage = chatMessage.getMessage();
        this.lastChatTime = chatMessage.getSendTime();
    }
}