package dingdong.dingdong.dto.chatpromise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
