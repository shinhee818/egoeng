package sh.egoeng.api.word.exception;

import org.springframework.http.HttpStatus;
import sh.egoeng.api.exception.ApiException;

public class WordTagNotFoundException extends ApiException {
    private static final String DEFAULT_MESSAGE = "태그를 찾을 수 없습니다. ID : %d";

    public WordTagNotFoundException(Long tagId) {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE.formatted(tagId));
    }
}

