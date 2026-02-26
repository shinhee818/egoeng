package sh.egoeng.api.word.controller.dto.response;

import sh.egoeng.domain.word.LearningStatus;

import java.time.LocalDateTime;

public record StudyCompleteResponse(
        Long userWordId,
        LearningStatus learningStatus,
        LocalDateTime lastStudiedAt
) {}














