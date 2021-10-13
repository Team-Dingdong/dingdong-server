package dingdong.dingdong.dto.auth;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsResponseDto {

    private String statusCode;
    private String statusName;
    private String requestId;
    private LocalDateTime requestTime;
}
