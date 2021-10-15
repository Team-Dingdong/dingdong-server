package dingdong.dingdong.dto.chat;

import java.time.LocalDate;
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
public class ChatPromiseRequestDto {

    // 약속 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate promiseDate;

    // 약속 날짜
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime promiseTime;

    // 약속 장소
    private String promiseLocal;
}
