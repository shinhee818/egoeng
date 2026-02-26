package sh.egoeng.api.quiz.controller.dto.response;

import java.util.List;

public record GenerateWordQuizResponse(
        Long quizId,
        String category,
        String level,
        List<WordQuizItem> words
) {
    public record WordQuizItem(
            Long wordId,
            String word,
            List<String> choices,
            Integer correctAnswerIndex
    ) {}
}

