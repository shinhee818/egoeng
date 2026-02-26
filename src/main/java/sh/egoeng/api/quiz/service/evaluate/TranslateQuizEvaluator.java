package sh.egoeng.api.quiz.service.evaluate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sh.egoeng.api.quiz.controller.dto.response.TranslateQuizEvaluationResponse;
import sh.egoeng.api.quiz.controller.dto.response.BaseEvaluationResponse;
import sh.egoeng.api.quiz.service.dto.TranslateEvaluateRequest;
import sh.egoeng.api.quiz.service.llm.LlmQuizClientService;
import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.service.UserQuizAnswerService;
import sh.egoeng.feign.llm.quiz.dto.response.TranslateResult;
import sh.egoeng.security.SecurityUtils;

@Component
@RequiredArgsConstructor
public class TranslateQuizEvaluator implements QuizEvaluator {
    private final LlmQuizClientService llmQuizClientService;
    private final UserQuizAnswerService userQuizAnswerService;

    @Override
    public QuizType getType() {
        return QuizType.TRANSLATE;
    }

    @Override
    public BaseEvaluationResponse evaluate(QuizEvaluateRequest req) {
        TranslateEvaluateRequest t = (TranslateEvaluateRequest) req;

        TranslateResult result = llmQuizClientService.callTranslateLLM(t);

        // 답변 저장 (번역 퀴즈는 perBlank 없음)
        userQuizAnswerService.saveAnswer(
                t.quizId(),
                SecurityUtils.currentId(),
                result.userAnswer(),
                result.correct(),
                null  // 번역 퀴즈는 perBlank 없음
        );

        return new TranslateQuizEvaluationResponse(
                t.quizId(),
                result.prompt(),
                result.userAnswer(),
                result.correctAnswer(),
                result.correct(),
                result.feedback()
        );
    }
}