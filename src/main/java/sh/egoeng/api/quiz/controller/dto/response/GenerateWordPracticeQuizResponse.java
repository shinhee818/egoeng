package sh.egoeng.api.quiz.controller.dto.response;

import java.util.List;

public record GenerateWordPracticeQuizResponse(
        Long quizId,
        String mode,
        List<WordPracticeQuizItem> words
) {}













