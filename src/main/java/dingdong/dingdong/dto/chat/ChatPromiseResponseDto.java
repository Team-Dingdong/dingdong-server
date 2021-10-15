package dingdong.dingdong.dto.chat;

import dingdong.dingdong.domain.chat.ChatPromise;
import dingdong.dingdong.domain.chat.PromiseType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatPromiseResponseDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate promiseDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime promiseTime;

    // 약속 장소
    private String promiseLocal;

    // 전체 인원
    private Integer totalPeople;

    // 투표 인원
    private Integer votingPeople;

    // 약속 마감 시간
    private LocalDateTime promiseEndTime;

    // 약속 상태
    private PromiseType type;

    public static ChatPromiseResponseDto from(ChatPromise chatPromise) {
        return ChatPromiseResponseDto.builder()
            .promiseDate(chatPromise.getPromiseDate())
            .promiseTime(chatPromise.getPromiseTime())
            .promiseLocal(chatPromise.getPromiseLocal())
            .totalPeople(chatPromise.getTotalPeople())
            .votingPeople(chatPromise.getVotingPeople())
            .promiseEndTime(chatPromise.getPromiseEndTime())
            .type(chatPromise.getType())
            .build();
    }
}
