package sh.egoeng.api.learning.controller.dto.response;

import sh.egoeng.api.word.controller.query.dto.TagInfo;
import sh.egoeng.domain.word.LearningStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 학습 단어 목록 조회 응답 (인피니트 스크롤)
 */
public record LearningWordQueryResponse(
        List<LearningWordItem> items,
        boolean hasNext,
        Long nextCursor
) {
    /**
     * 학습 단어 아이템
     */
    public record LearningWordItem(
            /**
             * 유저 단어 ID
             */
            Long id,

            /**
             * 영어 단어
             */
            String text,

            /**
             * 한국어 의미
             */
            String meaningKo,

            /**
             * 학습 상태 (NEW, LEARNING, REVIEWING, MASTERED)
             */
            LearningStatus learningStatus,

            /**
             * 암기 정도 (0-100)
             */
            Integer masteryLevel,

            /**
             * 복습 횟수
             */
            Integer reviewCount,

            /**
             * 다음 복습 예정일 (YYYY-MM-DD, MASTERED 상태일 경우 null)
             */
            LocalDate nextReviewDate,

            /**
             * 마지막 학습 시간 (ISO 8601, 학습하지 않은 경우 null)
             */
            LocalDateTime lastStudiedAt,

            /**
             * 즐겨찾기 여부
             */
            Boolean isFavourite,

            /**
             * 태그 목록
             */
            List<TagInfo> tags
    ) {}
}
