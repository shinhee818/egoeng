package sh.egoeng.api.word.controller.dto.response;

import java.time.LocalDateTime;

/**
 * 유저 단어 학습 완료 응답
 */
public record UserWordPracticeCompleteResponse(
        Long userWordId,
        Integer practiceCount,
        LocalDateTime lastPracticedAt
) {}













