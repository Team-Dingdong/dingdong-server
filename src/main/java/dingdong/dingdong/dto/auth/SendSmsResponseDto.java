package dingdong.dingdong.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSmsResponseDto {

    private String statusCode;
    private String statusName;
    private String requestId;
    private Timestamp requestTime;
}
