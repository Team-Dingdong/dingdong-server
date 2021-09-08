package dingdong.dingdong.domain.chat;


import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import dingdong.dingdong.service.chat.PromiseType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private int totalPeople;

    // 투표 인원
    private int votingPeople;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    public ChatPromise(ChatRoom chatRoom, ChatPromiseRequestDto chatPromiseRequestDto) {
        this.id = chatRoom.getId();
        this.chatRoom = chatRoom;
        this.promiseDate = chatPromiseRequestDto.getPromiseDate();
        this.promiseTime = chatPromiseRequestDto.getPromiseTime();
        this.promiseLocal = chatPromiseRequestDto.getPromiseLocal();
        this.type = PromiseType.PROGRESS;
        this.promiseEndTime = LocalDateTime.now().plusHours(3);
        this.totalPeople = chatRoom.getPost().getGatheredPeople();
        this.votingPeople = 1;
    }

    public void plusVotingPeople() {
        this.votingPeople = this.votingPeople == this.totalPeople ? this.votingPeople : this.votingPeople + 1;
        if(this.votingPeople == this.totalPeople) {
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
