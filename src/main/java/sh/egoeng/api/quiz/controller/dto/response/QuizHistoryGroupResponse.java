package sh.egoeng.api.quiz.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 퀴즈 히스토리 그룹 응답
 * UserQuiz 기준으로 그룹핑된 퀴즈 히스토리
 */
public record QuizHistoryGroupResponse(
        Long userQuizId,
        Long quizId,
        String quizType,
        Integer score,
        Integer totalQuestions,
        LocalDateTime submittedAt,
        List<QuizHistoryItemResponse> answers  // 각 문제별 답안 목록
) {
    /**
     * 각 문제별 답안 정보
     */
    public record QuizHistoryItemResponse(
            Long userQuizAnswerId,
            Long wordId,  // 단어 퀴즈의 경우 wordId, 다른 퀴즈는 null
            String word,  // 단어 퀴즈의 경우 단어 텍스트, 다른 퀴즈는 null
            String question,
            String koreanSentence,
            String userAnswer,
            String correctAnswer,
            Boolean correct,
            String feedback,
            LocalDateTime answeredAt,
            List<BlankAnswerDetail> blankAnswers  // 빈칸 퀴즈의 경우 각 빈칸별 답안 상세 정보
    ) {}
}

