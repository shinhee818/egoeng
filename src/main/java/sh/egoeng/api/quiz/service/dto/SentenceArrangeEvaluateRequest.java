package sh.egoeng.api.quiz.service.dto;

import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.QuizType;

public record SentenceArrangeEvaluateRequest(
        Long quizId,                 // 퀴즈 ID (어떤 퀴즈에 대한 답변인지)
        String userAnswer            // 사용자 답변 (공백으로 구분된 단어들)
) implements QuizEvaluateRequest {
    @Override
    public QuizType getQuizType() {
        return QuizType.SENTENCE_ARRANGE;
    }
}
