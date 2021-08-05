package dingdong.dingdong.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dingdong.dingdong.service.auth.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class authController {

    private final SmsService smsService;

    // 휴대폰 인증
    @PostMapping("/send-sms")
    void sendSms() throws NoSuchAlgorithmException, URISyntaxException, UnsupportedEncodingException, InvalidKeyException, JsonProcessingException {
        smsService.sendSms("01084071066", "메세지 보내기!");
    }
}
