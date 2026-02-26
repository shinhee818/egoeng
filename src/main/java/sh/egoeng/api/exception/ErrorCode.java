package sh.egoeng.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없음"),
    CONFLICT(HttpStatus.CONFLICT, "요청이 현재 상태와 충돌"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한 없음"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");

    private final HttpStatus status;
    private final String message;
}
