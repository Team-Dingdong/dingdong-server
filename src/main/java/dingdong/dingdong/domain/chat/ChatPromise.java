package dingdong.dingdong.domain.chat;


import dingdong.dingdong.domain.BaseTimeEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatPromise extends BaseTimeEntity {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate promiseDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime promiseTime;

    // 약속 장소
    private String promiseLocal;

    // 약속 상태
    private PromiseType type;

    // 전체 인원
    private Integer totalPeople;

    // 투표 인원
    private Integer votingPeople;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    public void plusVotingPeople() {
        this.votingPeople =
            this.votingPeople == this.totalPeople ? this.votingPeople : this.votingPeople + 1;
        if (this.votingPeople == this.totalPeople) {
            this.type = PromiseType.CONFIRMED;
        }
    }

    public void setPromiseDate(LocalDate date) {
        this.promiseDate = date;
    }

    public void setPromiseTime(LocalTime time) {
        this.promiseTime = time;
    }

    public void setPromiseLocal(String local) {
        this.promiseLocal = local;
    }

    public void updateAll() {
        this.type = PromiseType.PROGRESS;
        this.promiseEndTime = LocalDateTime.now().plusHours(3);
        this.totalPeople = chatRoom.getPost().getGatheredPeople();
        this.votingPeople = 1;
    }
}
