package dingdong.dingdong.util.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
public class Result<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final HttpStatus result;
    private final String code;
    private final String message;
    private final T data;

    public static Result of(ResultCode resultCode) {
        return Result.builder()
            .status(resultCode.getHttpStatus().value())
            .result(resultCode.getHttpStatus())
            .code(resultCode.name())
            .message(resultCode.getDetail())
            .build();
    }

    public static ResponseEntity<Result> toResult(ResultCode resultCode) {
        return ResponseEntity
            .status(resultCode.getHttpStatus())
            .body(Result.builder()
                .status(resultCode.getHttpStatus().value())
                .result(resultCode.getHttpStatus())
                .code(resultCode.name())
                .message(resultCode.getDetail())
                .build()
            );
    }

    public static <T> ResponseEntity<Result<T>> toResult(ResultCode resultCode, T data) {
        return ResponseEntity
            .status(resultCode.getHttpStatus())
            .body(Result.<T>builder()
                .status(resultCode.getHttpStatus().value())
                .result(resultCode.getHttpStatus())
                .code(resultCode.name())
                .message(resultCode.getDetail())
                .data(data)
                .build()
            );
    }

}
