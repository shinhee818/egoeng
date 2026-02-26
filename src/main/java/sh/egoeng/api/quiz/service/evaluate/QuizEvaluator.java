package sh.egoeng.api.quiz.service.evaluate;

import sh.egoeng.api.quiz.controller.dto.response.BaseEvaluationResponse;
import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.QuizType;

/**
 * 정답 확인 && 유저 퀴즈 결과 저장
 */
public interface QuizEvaluator {
    QuizType getType();
    BaseEvaluationResponse evaluate(QuizEvaluateRequest request);
}