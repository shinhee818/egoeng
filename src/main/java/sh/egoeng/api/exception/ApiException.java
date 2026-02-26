package sh.egoeng.api.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    // ErrorCode를 통한 생성자
    protected ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

    protected ApiException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

    // ErrorCode + 커스텀 메시지
    protected ApiException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
        this.message = customMessage;
    }

    protected ApiException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
        this.message = customMessage;
    }

    // HttpStatus + 메시지 직접 받기
    protected ApiException(HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = null;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    protected ApiException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
