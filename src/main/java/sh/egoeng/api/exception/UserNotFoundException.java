package sh.egoeng.api.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    private static final String DEFAULT_MESSAGE = "유저를 찾을 수 없습니다. ID: %d";

    public UserNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE.formatted(userId));
    }
}
