package dingdong.dingdong.domain.chat;

import dingdong.dingdong.domain.post.Post;
import lombok.*;

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
public class ChatRoom {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime endDate;

    private String lastChatMessage;

    private LocalDateTime lastChatTime;

    private int userCount;

    private int maximumUserConunt;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messages = new ArrayList<>();

    public void plusUserCount() {
        this.userCount = this.userCount == this.maximumUserConunt ? this.userCount : this.userCount + 1;
    }

    public void minusUserCount() {
        this.userCount = this.userCount == 0 ? 0 : this.userCount - 1;
    }

    public ChatRoom(Post post) {
        this.id = post.getId();
        this.post = post;
        this.userCount = 1;
        this.maximumUserConunt = post.getGatheredPeople();
    }
}