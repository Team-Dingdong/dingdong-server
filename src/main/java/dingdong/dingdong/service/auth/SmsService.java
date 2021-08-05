package dingdong.dingdong.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.dto.auth.ApplicationNaverSENS;
import dingdong.dingdong.dto.auth.MessageRequestDto;
import dingdong.dingdong.dto.auth.SendSmsResponseDto;
import dingdong.dingdong.dto.auth.SendSmsRequestDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Service
public class SmsService {

    private final ApplicationNaverSENS applicationNaverSENS;

    public SendSmsResponseDto sendSms(String recipientPhoneNumber, String content) throws JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {
        Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();
        List<MessageRequestDto> messages = new ArrayList<>();

        // 보내는 사람에게 내용을 보냄
        messages.add(new MessageRequestDto(recipientPhoneNumber, content));

        // 전체 json에 대해 메시지를 만든다.
        SendSmsRequestDto sendSmsRequestDto = new SendSmsRequestDto("SMS", "COMM", "82", applicationNaverSENS.getSendFrom(), "Test message", messages);

        // 쌓아온 바디를 json 형태로 변환시켜준다.
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(sendSmsRequestDto);

        // 헤더에서 여러 설정값들을 잡아준다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", applicationNaverSENS.getAccessKey());

        // 제일 중요한 signature 서명하기.
        String sig = makeSignature(time);
        log.info("sig -> " + sig);
        headers.set("x-ncp-apigw-signature-v2", sig);

        // 위에서 조립한 jsonBody와 헤더를 조립한다.
        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);
        log.info(body.getBody());

        // restTemplate로 post 요청을 보낸다. 성공하면 202 코드가 반환된다.
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SendSmsResponseDto sendSmsResponseDto = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+applicationNaverSENS.getServiceId()+"/messages"), body, SendSmsResponseDto.class);
        log.info(sendSmsResponseDto.getStatusCode());

        return sendSmsResponseDto;
    }

    public String makeSignature(Long time) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+applicationNaverSENS.getServiceId()+"/messages";
        String timestamp = time.toString();
        String accessKey = applicationNaverSENS.getAccessKey();
        String secretKey = applicationNaverSENS.getSecretKey();

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }
}
