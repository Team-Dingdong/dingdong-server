package dingdong.dingdong.util.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateException.class)
    protected ResponseEntity<Result> handleDuplicateException(DuplicateException e) {
        log.error("handleDuplicateException : {}", e.getResultCode());
        return Result.toResult(e.getResultCode());
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
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

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Result> handleAuthenticationException(AuthenticationException e) {
        log.error("handleAuthenticationException : {}", e.getMessage());
        if (e instanceof BadCredentialsException) {
            return Result.toResult(ResultCode.AUTH_FAIL);
        } else if (e instanceof InternalAuthenticationServiceException) {
            return Result.toResult(ResultCode.AUTH_NOT_FOUND);
        } else if (e instanceof UsernameNotFoundException) {
            return Result.toResult(ResultCode.AUTH_NOT_FOUND);
        }
        return Result.toResult(ResultCode.AUTH_ERROR);
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> allFieldErrors = ex.getBindingResult().getFieldErrors();
        List<Map<String, String>> data = new ArrayList<>();
        for (FieldError fieldError : allFieldErrors) {
            Map<String, String> item = new HashMap<>();
            item.put(fieldError.getField(), fieldError.getDefaultMessage());
            data.add(item);
        }
        return Result.toResult(ResultCode.VALID_ERROR, data);
    }
}
