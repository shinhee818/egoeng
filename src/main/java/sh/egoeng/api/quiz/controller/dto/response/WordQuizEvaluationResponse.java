package sh.egoeng.api.quiz.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 단어 퀴즈 평가 결과
 * 전체 점수와 정답률만 반환합니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WordQuizEvaluationResponse implements BaseEvaluationResponse {
    private final Long quizId;
    private final boolean correct;        // 전체 정답 여부
    private final String feedback;        // 정답률: X/Y (Z%)

    public WordQuizEvaluationResponse(
            Long quizId,
            boolean correct,
            String feedback) {
        this.quizId = quizId;
        this.correct = correct;
        this.feedback = feedback;
    }

    @Override
    public Long quizId() {
        return quizId;
    }

    @Override
    public String prompt() {
        return null;
    }

    @Override
    public String userAnswer() {
        return null;
    }

    @Override
    public String correctAnswer() {
        return null;
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

