package dingdong.dingdong.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtAuthException extends RuntimeException {

    private final ResultCode resultCode;
}
