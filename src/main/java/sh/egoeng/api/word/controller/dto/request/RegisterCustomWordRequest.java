package sh.egoeng.api.word.controller.dto.request;

import java.util.List;

/**
 * 커스텀 단어 등록 요청
 */
public record RegisterCustomWordRequest(
        String word,           // 영어 단어 (필수)
        String meaningKo,      // 한국어 뜻 (필수)
        List<String> tags      // 태그 이름 리스트 (선택사항)
) {
    public void validate() {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("단어는 필수입니다.");
        }
        if (meaningKo == null || meaningKo.trim().isEmpty()) {
            throw new IllegalArgumentException("뜻은 필수입니다.");
        }
    }
}











