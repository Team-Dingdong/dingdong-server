package dingdong.dingdong.util.exception;

import dingdong.dingdong.dto.auth.AuthResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthenticationException extends RuntimeException {

    private final ResultCode resultCode;
    private AuthResponseDto authResponseDto;
}
