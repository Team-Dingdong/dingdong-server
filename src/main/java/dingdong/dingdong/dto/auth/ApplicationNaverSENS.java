package dingdong.dingdong.dto.auth;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ApplicationNaverSENS {

    @Value("${application-naver-sens.send-from}")
    private String sendFrom;

    @Value("${application-naver-sens.access-key}")
    private String accessKey;

    @Value("${application-naver-sens.secret-key}")
    private String secretKey;

    @Value("${application-naver-sens.service-id}")
    private String serviceId;
}
