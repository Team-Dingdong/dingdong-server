package dingdong.dingdong.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LimitException extends RuntimeException {

    private final ResultCode resultCode;
}
