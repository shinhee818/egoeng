package sh.egoeng.api.word.exception;

import org.springframework.http.HttpStatus;
import sh.egoeng.api.exception.ApiException;
import sh.egoeng.api.exception.ErrorCode;

public class UserWordNotFoundException extends ApiException {
    private static final String DEFAULT_MESSAGE = "등록 되지 않은 단어입니다. ID : %d";

    public UserWordNotFoundException(Long wordId) {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE.formatted(wordId));
    }
}
