package sh.egoeng.api.quiz.service.dto;

import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.QuizType;

import java.util.List;

public record WordQuizEvaluateRequest(
        Long quizId,
        List<WordAnswer> answers
) implements QuizEvaluateRequest {
    public record WordAnswer(
            Long wordId,
            Integer selectedIndex
    ) {}

    @Override
    public QuizType getQuizType() {
        return QuizType.WORD_QUIZ;
    }
}













