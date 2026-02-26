package sh.egoeng.api.word.controller.dto.request;

import java.util.List;

public record RegisterUserWordRequest(
        Long wordId,  // 기존 Word 참조 (선택사항, wordId가 null이면 custom 필드 필수)
        
        // 커스텀 단어 필드 (wordId가 null일 때 필수)
        String text,
        String meaningKo,
        
        List<String> tagNames  // 태그 이름 리스트 (선택사항)
) {
    public void validate() {
        if (wordId == null) {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("text is required when wordId is null");
            }
            if (meaningKo == null || meaningKo.trim().isEmpty()) {
                throw new IllegalArgumentException("meaningKo is required when wordId is null");
            }
        }
    }
}

