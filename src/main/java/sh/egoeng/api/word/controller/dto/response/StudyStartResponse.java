package sh.egoeng.api.word.controller.dto.response;

import sh.egoeng.domain.word.LearningStatus;

import java.time.LocalDateTime;

public record StudyStartResponse(
        Long userWordId,
        LearningStatus learningStatus,
        LocalDateTime lastStudiedAt
) {}
















