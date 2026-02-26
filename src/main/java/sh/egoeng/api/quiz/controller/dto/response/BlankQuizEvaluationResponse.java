package sh.egoeng.api.quiz.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 빈칸 채우기 퀴즈 평가 결과
 * 각 빈칸별 상세 정보를 포함합니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlankQuizEvaluationResponse implements BaseEvaluationResponse {
    private final Long quizId;
    private final String prompt;
    private final String userAnswer;      // 띄어쓰기로 연결된 사용자 답안
    private final String correctAnswer;   // 완전한 정답 문장
    private final boolean correct;
    private final String feedback;
    private final List<String> llmAnswer;    // LLM 모범답안 배열
    private final List<String> correctWords; // 각 빈칸의 정답 단어 배열
    private final List<String> userAnswers;  // 사용자 답안 배열
    private final List<Boolean> perBlank;    // 각 빈칸별 정답 여부

    public BlankQuizEvaluationResponse(
            Long quizId,
            String prompt,
            String userAnswer,
            String correctAnswer,
            boolean correct,
            String feedback,
            List<String> llmAnswer,
            List<String> correctWords,
            List<String> userAnswers,
            List<Boolean> perBlank) {
        this.quizId = quizId;
        this.prompt = prompt;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.correct = correct;
        this.feedback = feedback;
        this.llmAnswer = llmAnswer;
        this.correctWords = correctWords;
        this.userAnswers = userAnswers;
        this.perBlank = perBlank;
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

    public List<String> getLlmAnswer() {
        return llmAnswer;
    }

    public List<String> getCorrectWords() {
        return correctWords;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public List<Boolean> getPerBlank() {
        return perBlank;
    }
}

