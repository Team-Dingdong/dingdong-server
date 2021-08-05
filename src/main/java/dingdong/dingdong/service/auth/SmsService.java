package dingdong.dingdong.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dingdong.dingdong.dto.auth.*;
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
import java.util.Random;

@Slf4j
@Data
@Service
public class SmsService {

    private final ApplicationNaverSENS applicationNaverSENS;

    public SendSmsResponseDto sendSms(MessageRequestDto messageRequestDto) throws JsonProcessingException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {
        Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String random = makeRandom();
        String content = String.format("[띵-동] 인증번호 [%s] *타인에게 노출하지 마세요.", random);
        SendSmsMessage sendSmsMessage = new SendSmsMessage(messageRequestDto.getTo(), content);
        List<SendSmsMessage> messages = new ArrayList<>();

        // 보내는 사람에게 내용을 보냄
        messages.add(sendSmsMessage);

        // 전체 json에 대해 메시지를 만든다.
        SendSmsRequestDto sendSmsRequestDto = new SendSmsRequestDto("SMS", "COMM", "82", applicationNaverSENS.getSendFrom(), "Default message", messages);

        // 쌓아온 바디를 json 형태로 변환시켜준다.
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(sendSmsRequestDto);

        // 헤더에서 여러 설정값들을 잡아준다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", applicationNaverSENS.getAccessKey());

        // 제일 중요한 signature 서명하기.
        String sign = makeSignature(time);
        log.info("signature -> " + sign);
        headers.set("x-ncp-apigw-signature-v2", sign);

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

    String makeRandom() {
        Random rand = new Random();
        String numStr = "";

        for(int i=0; i<6; i++) {

            // 0~9까지 난수 생성
            String num = Integer.toString(rand.nextInt(10));

            // 중복된 값이 있는지 검사한다
            if(!numStr.contains(num)) {
                // 중복된 값이 없으면 numStr에 append
                numStr += num;
            } else {
                // 생성된 난수가 중복되면 루틴을 다시 실행한다
                i -= 1;
            }
        }
        return numStr;
    }
}
