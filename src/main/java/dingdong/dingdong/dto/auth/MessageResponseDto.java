package dingdong.dingdong.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
