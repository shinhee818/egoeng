package sh.egoeng.api.quiz.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 문장 배열 퀴즈 평가 결과
 * 사용자가 배열한 문장과 정답 순서를 반환합니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SentenceArrangeEvaluationResponse implements BaseEvaluationResponse {
    private final Long quizId;
    private final String prompt;          // 정답 문장 (정규화된 버전)
    private final String userAnswer;      // 사용자가 배열한 문장
    private final String correctAnswer;   // 정답 (정규화된 버전)
    private final boolean correct;
    private final String feedback;

    public SentenceArrangeEvaluationResponse(
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

