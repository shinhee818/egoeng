package sh.egoeng.feign.llm.quiz.dto.response;

public record TranslateResult(
        String prompt,
        String userAnswer,
        String correctAnswer,
        boolean correct,
        String feedback
) {
}