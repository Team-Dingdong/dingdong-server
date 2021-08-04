package dingdong.dingdong.controller;

import dingdong.dingdong.dto.auth.LoginRequestDto;
import dingdong.dingdong.dto.auth.SignupRequestDto;
import dingdong.dingdong.dto.auth.TokenDto;
import dingdong.dingdong.service.AuthService;
import dingdong.dingdong.util.exception.Result;
import dingdong.dingdong.util.exception.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Result<TokenDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
        TokenDto data = authService.login(loginRequestDto);
        return Result.toResult(ResultCode.LOGIN_SUCCESS, data);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Result> signup(@RequestBody SignupRequestDto signupRequestDto) {
        authService.signup(signupRequestDto);
        return Result.toResult(ResultCode.SIGNUP_SUCCESS);
    }
}
