package sh.egoeng.api.word.controller.dto.response;

import sh.egoeng.domain.word.LearningStatus;

import java.time.LocalDateTime;

public record StudyMasterResponse(
        Long userWordId,
        LearningStatus learningStatus,
        Integer masteryLevel,
        LocalDateTime lastStudiedAt
) {}
















