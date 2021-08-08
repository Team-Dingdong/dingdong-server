package dingdong.dingdong.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dingdong.dingdong.dto.auth.CheckRequestDto;
import dingdong.dingdong.dto.auth.MessageRequestDto;
import dingdong.dingdong.dto.auth.MessageResponseDto;
import dingdong.dingdong.service.auth.AuthService;
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

    private final AuthService authService;

    // 휴대폰 인증 번호 전송
    @PostMapping("/send-sms")
    public ResponseEntity<Result<MessageResponseDto>> sendSms(@RequestBody MessageRequestDto messageRequestDto) throws NoSuchAlgorithmException, URISyntaxException, UnsupportedEncodingException, InvalidKeyException, JsonProcessingException {
        MessageResponseDto data = authService.sendSms(messageRequestDto);
        return Result.toResult(ResultCode.SEND_SMS_SUCCESS, data);
    }

    // 휴대폰 인증 번호 확인
    @PostMapping("/check")
    public ResponseEntity<Result> check(@RequestBody CheckRequestDto checkRequestDto) {

        return Result.toResult(ResultCode.LOGIN_SUCCESS);
    }
}
