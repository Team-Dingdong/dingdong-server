package dingdong.dingdong.domain.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import dingdong.dingdong.domain.BaseTimeEntity;
import dingdong.dingdong.domain.post.Post;
import dingdong.dingdong.dto.chatpromise.ChatPromiseRequestDto;
import lombok.*;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

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
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    // 약속 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date promiseDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time promiseTime;

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

    public void setChatPromise(ChatPromiseRequestDto request){
        this.promiseDate = request.getPromiseDate();
        this.promiseTime = request.getPromiseTime();
        this.promiseLocal = request.getPromiseLocal();
    }

    public ChatPromise(Post post) {
        this.id = post.getId();
        this.post = post;
    }
}
