package dingdong.dingdong.domain.chat;


import dingdong.dingdong.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatPromise extends BaseTimeEntity {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime promiseDateTime;

    // 약속 장소
    private String promiseLocal;

    // 약속 상태
    private String type;

    // 전체 인원
    private int totalPeople;

    // 투표 인원
    private int votingPeople;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    public ChatPromise(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.chatRoom = chatRoom;
        this.totalPeople = chatRoom.getPost().getGatheredPeople();
        this.votingPeople = 1;
    }

    public void plusVotingPeople() {
        this.votingPeople = this.votingPeople == this.totalPeople ? this.votingPeople : this.votingPeople + 1;
    }
}
