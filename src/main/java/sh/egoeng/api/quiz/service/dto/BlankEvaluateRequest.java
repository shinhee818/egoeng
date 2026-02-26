package sh.egoeng.api.quiz.service.dto;

import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.QuizType;

import java.util.List;

public record BlankEvaluateRequest(
        Long quizId,
        String sentence,
        List<String> blanks,
        String userAnswer
) implements QuizEvaluateRequest {
    @Override
    public QuizType getQuizType() {
        return QuizType.BLANK;
    }
}