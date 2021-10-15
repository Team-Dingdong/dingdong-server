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
public class MessageResponseDto {

    private String requestId;
    private LocalDateTime requestTime;

    public static MessageResponseDto from(SendSmsResponseDto sendSmsResponseDto) {
        return MessageResponseDto.builder()
            .requestId(sendSmsResponseDto.getRequestId())
            .requestTime(sendSmsResponseDto.getRequestTime())
            .build();
    }
}
