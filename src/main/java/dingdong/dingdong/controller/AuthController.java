package dingdong.dingdong.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dingdong.dingdong.dto.auth.MessageRequestDto;
import dingdong.dingdong.dto.auth.SendSmsResponseDto;
import dingdong.dingdong.service.auth.SmsService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SmsService smsService;

    // 휴대폰 인증
    @PostMapping("/send-sms")
    public ResponseEntity<Result<SendSmsResponseDto>> sendSms(@RequestBody MessageRequestDto messageRequestDto) throws NoSuchAlgorithmException, URISyntaxException, UnsupportedEncodingException, InvalidKeyException, JsonProcessingException {
        SendSmsResponseDto data = smsService.sendSms(messageRequestDto);
        return Result.toResult(ResultCode.SENDSMS_SUCCESS, data);
    }
}
