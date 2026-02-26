package sh.egoeng.api.quiz.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record QuizHistoryResponse(
        Long quizId,
        Long questionId,  // 현재 구조에는 없으므로 quizId 사용
        String quizType,
        String question,
        String koreanSentence,
        String userAnswer,
        String correctAnswer,
        Boolean correct,
        String feedback,
        LocalDateTime submittedAt,
        List<BlankAnswerDetail> blankAnswers  // 빈칸 퀴즈의 경우 각 빈칸별 답안 상세 정보
) {}

