package sh.egoeng.domain.word.exception;

import sh.egoeng.domain.exception.DomainException;

public class UserWordAlreadyExistsException extends DomainException {
    private static final String DEFAULT_MESSAGE = "이미 저장된 단어입니다. ID : %d";

    public UserWordAlreadyExistsException(Long wordId) {
        super(DEFAULT_MESSAGE.formatted(wordId));
    }
}