package sh.egoeng.api.quiz.controller.dto.request.evaluate;

import sh.egoeng.api.quiz.service.dto.SentenceArrangeEvaluateRequest;

public record SentenceArrangeQuizRequest(
        Long quizId,                 // 퀴즈 ID (어떤 퀴즈에 대한 답변인지)
        String userAnswer            // 사용자 답변 (공백으로 구분된 단어들, 예: "I love you")
) {
    public SentenceArrangeEvaluateRequest toServiceRequest() {
        return new SentenceArrangeEvaluateRequest(
                quizId,
                userAnswer
        );
    }
}
