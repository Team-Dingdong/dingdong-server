package dingdong.dingdong.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.auth.*;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // 휴대폰 인증 번호 전송
    @PostMapping("/send-sms")
    public ResponseEntity<Result<MessageResponseDto>> sendSms(@RequestBody MessageRequestDto messageRequestDto) throws NoSuchAlgorithmException, URISyntaxException, UnsupportedEncodingException, InvalidKeyException, JsonProcessingException {
        MessageResponseDto data = authService.sendSms(messageRequestDto);
        return Result.toResult(ResultCode.SEND_SMS_SUCCESS, data);
    }

    // 휴대폰 인증 번호 확인, 로그인 or 회원가입
    @PostMapping("")
    public ResponseEntity<Result<TokenDto>> auth(@RequestBody AuthRequestDto authRequestDto) {
        Map<AuthType, TokenDto> data = authService.auth(authRequestDto);
        if(data.containsKey(AuthType.LOGIN)) {
            return Result.toResult(ResultCode.LOGIN_SUCCESS, data.get(AuthType.LOGIN));
        } else {
            return Result.toResult(ResultCode.SIGNUP_SUCCESS, data.get(AuthType.SIGNUP));
        }
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<Result<TokenDto>> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenDto data = authService.reissue(tokenRequestDto);
        return Result.toResult(ResultCode.REISSUE_SUCCESS, data);
    }

    // 닉네임 설정
    @PostMapping("/nickname")
    public ResponseEntity<Result> nickname(@CurrentUser User user, @RequestBody NicknameRequestDto nicknameRequestDto) {
        authService.setNickname(user, nicknameRequestDto);
        return Result.toResult(ResultCode.NICKNAME_CREATE_SUCCESS);
    }

    // 동네 인증
    @PostMapping("/local")
    public ResponseEntity<Result> local(@CurrentUser User user, @RequestBody LocalRequestDto localRequestDto) {
        authService.setLocal(user, localRequestDto);
        return Result.toResult(ResultCode.LOCAL_CREATE_SUCCESS);
    }

    // 탈퇴하기
    @PatchMapping("/unsubscribe")
    public ResponseEntity<Result> unsubscribeUser (@CurrentUser User user){
        authService.unsubscribeUser(user.getId());
        return Result.toResult(ResultCode.UNSUBSCRIBE_SUCCESS);
    }
}
