package sh.egoeng.api.word.controller.dto.response;

import sh.egoeng.domain.word.LearningStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudyReviewResponse(
        Long userWordId,
        Boolean isCorrect,
        LearningStatus learningStatus,
        Integer reviewCount,
        LocalDate nextReviewDate,
        Integer masteryLevel,
        LocalDateTime lastStudiedAt
) {}
















