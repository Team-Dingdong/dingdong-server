package dingdong.dingdong.domain.chat;


import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatPromise extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatPromise_id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // 약속 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate promiseDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime promiseTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime promiseDateTime;

    // 약속 장소
    private String promiseLocal;

    // 약속 상태
    private String type;

    // 전체 인원
    private Long totalPeople;

    // 투표 인원
    private Long votingPeople;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    // 채팅방 마감 시간
    private LocalDateTime ChatRoomEndTime;

    @OneToMany(mappedBy = "chatPromise", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> user = new ArrayList<>();

    public void setChatPromise(ChatPromiseRequestDto request){
        this.promiseDate = request.getPromiseDate();
        this.promiseTime = request.getPromiseTime();
        this.promiseLocal = request.getPromiseLocal();
    }

    public ChatPromise(Post post, List<User> users) {
        this.id = post.getId();
        this.post = post;
        this.user = users;
    }
}
