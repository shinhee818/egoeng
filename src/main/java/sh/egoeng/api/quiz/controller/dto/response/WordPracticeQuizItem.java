package sh.egoeng.api.quiz.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record WordPracticeQuizItem(
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













