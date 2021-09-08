package dingdong.dingdong.dto.chatpromise;

import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.chat.PromiseType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ChatPromiseResponseDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate promiseDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime promiseTime;

    // 약속 장소
    private String promiseLocal;

    // 전체 인원
    private int totalPeople;

    // 투표 인원
    private int votingPeople;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    private PromiseType type;

    public ChatPromiseResponseDto(ChatPromise chatPromise){
        this.promiseDate = chatPromise.getPromiseDate();
        this.promiseTime = chatPromise.getPromiseTime();
        this.promiseLocal = chatPromise.getPromiseLocal();
        this.totalPeople = chatPromise.getTotalPeople();
        this.votingPeople = chatPromise.getVotingPeople();
        this.promiseEndTime = chatPromise.getPromiseEndTime();
        this.type = chatPromise.getType();
    }
}
