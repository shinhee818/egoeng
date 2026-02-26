package sh.egoeng.api.quiz.service.llm.dto;

import sh.egoeng.domain.quiz.QuizType;

public interface QuizEvaluateRequest {
    QuizType getQuizType();
}
