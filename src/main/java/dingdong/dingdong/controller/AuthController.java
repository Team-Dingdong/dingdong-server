package dingdong.dingdong.controller;

import dingdong.dingdong.domain.user.CurrentUser;
import dingdong.dingdong.domain.user.User;
import dingdong.dingdong.dto.auth.AuthRequestDto;
import dingdong.dingdong.dto.auth.LocalRequestDto;
import dingdong.dingdong.dto.auth.LocalResponseDto;
import dingdong.dingdong.dto.auth.MessageRequestDto;
import dingdong.dingdong.dto.auth.MessageResponseDto;
import dingdong.dingdong.dto.auth.NicknameRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.dto.auth.TokenRequestDto;
import dingdong.dingdong.service.auth.AuthService;
import dingdong.dingdong.service.auth.AuthType;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // 휴대폰 인증 번호 전송
    @PostMapping("/send-sms")
    public ResponseEntity<Result<MessageResponseDto>> sendSms(@RequestBody MessageRequestDto messageRequestDto) {
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
    @PatchMapping("/nickname")
    public ResponseEntity<Result> nickname(@CurrentUser User user, @RequestBody NicknameRequestDto nicknameRequestDto) {
        authService.setNickname(user, nicknameRequestDto);
        return Result.toResult(ResultCode.NICKNAME_CREATE_SUCCESS);
    }

    // 동네 목록 조회
    @GetMapping("/local")
    public ResponseEntity<Result<List<LocalResponseDto>>> getLocals(@RequestParam String city, @RequestParam String district) {
        List<LocalResponseDto> data = authService.getLocals(city, district);
        return Result.toResult(ResultCode.LOCAL_READ_SUCCESS, data);
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
        authService.unsubscribeUser(user);
        return Result.toResult(ResultCode.UNSUBSCRIBE_SUCCESS);
    }
}
