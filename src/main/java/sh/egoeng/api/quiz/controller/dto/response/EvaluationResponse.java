package sh.egoeng.api.quiz.controller.dto.response;

import java.util.List;

public record EvaluationResponse(
        Long quizId,           // 어떤 퀴즈에 대한 평가인지
        String prompt,
        String userAnswer,  // 사용자 답안 (띄어쓰기로 구분된 문자열)
        String correctAnswer,  // 완전한 정답 문장 (모범답안)
        boolean correct,
        String feedback,
        List<String> llmAnswer,  // LLM이 추론한 모범답안 배열 (빈칸 퀴즈의 경우)
        List<String> correctWords,  // 각 빈칸의 정답 단어 배열 (빈칸 퀴즈의 경우)
        List<String> userAnswers,  // 사용자 답안 배열 (빈칸 퀴즈의 경우)
        List<Boolean> perBlank  // 각 빈칸별 정답 여부 (빈칸 퀴즈의 경우)
) {}
