package sh.egoeng.api.quiz.service.dto;

import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.QuizType;

/**
 * @param quizId         퀴즈 ID
 * @param koreanSentence  원문(한국어)
 * @param userAnswer  사용자가 번역한 문장
 **/
public record TranslateEvaluateRequest(
        Long quizId,
        String koreanSentence,
        String userAnswer
) implements QuizEvaluateRequest {

    @Override
    public QuizType getQuizType() {
        return QuizType.TRANSLATE;
    }
}
