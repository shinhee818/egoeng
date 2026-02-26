package sh.egoeng.domain.word.exception;

import org.springframework.http.HttpStatus;
import sh.egoeng.api.exception.ApiException;

public class WordNotFoundException extends ApiException {
    private static final HttpStatus status = HttpStatus.NOT_FOUND;
    private static final String message = "단어를 찾을 수 없습니다. ID: %s";
    
    public WordNotFoundException(Long wordId) {
        super(status, message.formatted(wordId));
    }
}
















