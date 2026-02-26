package sh.egoeng.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sh.egoeng.api.exception.ApiException;
import sh.egoeng.api.exception.ErrorCode;
import sh.egoeng.api.exception.ErrorResponse;
import sh.egoeng.domain.exception.DomainException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(ApiException e) {
        // ErrorCode가 있으면 사용, 없으면 HttpStatus와 message 직접 사용
        if (e.getErrorCode() != null) {
            ErrorCode code = e.getErrorCode();
            return ResponseEntity
                    .status(code.getStatus())
                    .body(new ErrorResponse(code.name(), code.getMessage()));
        } else {
            // HttpStatus와 message를 직접 받은 경우
            return ResponseEntity
                    .status(e.getHttpStatus())
                    .body(new ErrorResponse(e.getHttpStatus().name(), e.getMessage()));
        }
    }

    // 도메인 예외 Default: 400 Bad Request로 처리
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "알 수 없는 오류"));
    }
}
