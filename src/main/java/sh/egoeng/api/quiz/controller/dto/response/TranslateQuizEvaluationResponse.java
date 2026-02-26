package sh.egoeng.api.quiz.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 번역 퀴즈 평가 결과
 * 사용자 답변과 정답을 간단하게 반환합니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranslateQuizEvaluationResponse implements BaseEvaluationResponse {
    private final Long quizId;
    private final String prompt;
    private final String userAnswer;
    private final String correctAnswer;
    private final boolean correct;
    private final String feedback;

    public TranslateQuizEvaluationResponse(
            Long quizId,
            String prompt,
            String userAnswer,
            String correctAnswer,
            boolean correct,
            String feedback) {
        this.quizId = quizId;
        this.prompt = prompt;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
        this.feedback = feedback;
    }

    @Override
    public Long quizId() {
        return quizId;
    }

    @Override
    public String prompt() {
        return prompt;
    }

    @Override
    public String userAnswer() {
        return userAnswer;
    }

    @Override
    public String correctAnswer() {
        return correctAnswer;
    }

    @Override
    public boolean correct() {
        return correct;
    }

    @Override
    public String feedback() {
        return feedback;
    }
}

