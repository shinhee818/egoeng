package sh.egoeng.api.exception;

import org.springframework.http.HttpStatus;

public class WordNotFoundException extends ApiException {
    private static final String DEFAULT_MESSAGE = "단어를 찾을 수 없습니다. ID: %d";

    public WordNotFoundException(Long wordId) {
        super(HttpStatus.NOT_FOUND, DEFAULT_MESSAGE.formatted(wordId));
    }
}















