package dingdong.dingdong.util.exception;

import dingdong.dingdong.dto.auth.AuthResponseDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateException.class)
    protected ResponseEntity<Result> handleDuplicateException(DuplicateException e) {
        log.error("handleDuplicateException : {}", e.getResultCode());
        return Result.toResult(e.getResultCode());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Result> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.error("handleResourceNotFoundException : {}", e.getResultCode());
        return Result.toResult(e.getResultCode());
    }

    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<Result> handleForbiddenException(ForbiddenException e) {
        log.error("handleForbiddenException : {}", e.getResultCode());
        return Result.toResult(e.getResultCode());
    }

    @ExceptionHandler(JwtAuthException.class)
    protected ResponseEntity<Result> handleJwtAuthException(JwtAuthException e) {
        log.error("handleJwtAuthException : {}", e.getResultCode());
        return Result.toResult(e.getResultCode());
    }

    @ExceptionHandler(LimitException.class)
    protected ResponseEntity<Result> handleLimitException(LimitException e) {
        log.error("handleLimitException : {}", e.getResultCode());
        return Result.toResult(e.getResultCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Result> handleBadCredentialsException(
        BadCredentialsException e) {
        log.error("handleBadCredentialsException : {}", e.getMessage());
        return Result.toResult(ResultCode.AUTH_FAIL);
    }

    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<Result> handleDisabledException(DisabledException e) {
        log.error("handleDisabledException : {}", e.getMessage());
        return Result.toResult(ResultCode.INVALID_ACCOUNT);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    protected ResponseEntity<Result> handleCredentialsExpiredException(
        CredentialsExpiredException e) {
        log.error("handleCredentialsExpiredException : {}", e.getMessage());
        return Result.toResult(ResultCode.CREDENTIALS_EXPIRED);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Result<AuthResponseDto>> handleAuthenticationException(AuthenticationException e) {
        log.error("handleAuthenticationException : {}", e.getResultCode());
        if (e.getResultCode() == ResultCode.AUTH_FAIL) {
            return Result.toResult(e.getResultCode(), e.getAuthResponseDto());
        }
        return Result.toResult(e.getResultCode(), null);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<Result> handleIllegalStateException(IllegalStateException e) {
        log.error("handleIllegalStateException : {}", e.getMessage());
        return Result.toResult(ResultCode.INTER_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> allFieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> data = new HashMap<>();
        for (FieldError fieldError : allFieldErrors) {
            data.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return Result.toResult(ResultCode.VALID_ERROR, data);
    }
}
