package sh.egoeng.api.word.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 유저 단어 학습 시작 응답
 */
public record UserWordPracticeResponse(
        List<UserWordPracticeItem> words
) {
    public record UserWordPracticeItem(
            Long userWordId,
            String word,
            List<WordMeaning> meanings,
            Integer practiceCount,
            LocalDateTime lastPracticedAt
    ) {
        public record WordMeaning(
                Long id,
                String meaningKo,
                String partOfSpeech,
                String example
        ) {}
    }
}













