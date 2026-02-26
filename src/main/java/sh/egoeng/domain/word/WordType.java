package sh.egoeng.domain.word;

/**
 * 단어 타입
 *
 * SEARCH: 검색해서 등록한 단어 (Word 테이블에 있음)
 * CUSTOM: 사용자가 직접 입력한 단어 (Word 테이블에 없음, customText/customMeaningKo만 있음)
 */
public enum WordType {
    SEARCH("검색 단어"),
    CUSTOM("커스텀 단어");

    private final String description;

    WordType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

