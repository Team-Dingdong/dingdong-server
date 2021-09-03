package dingdong.dingdong.dto.chatpromise;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class ChatPromiseRequestDto {

    // 약속 날짜
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate promiseDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime promiseTime;

    // 약속 장소
    private String promiseLocal;
}
