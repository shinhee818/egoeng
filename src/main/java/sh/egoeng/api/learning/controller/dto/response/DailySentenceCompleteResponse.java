package sh.egoeng.api.learning.controller.dto.response;

import java.time.LocalDateTime;

public record DailySentenceCompleteResponse(
        Long userSentenceId,
        Integer learningCount,
        LocalDateTime learnedAt
) {}