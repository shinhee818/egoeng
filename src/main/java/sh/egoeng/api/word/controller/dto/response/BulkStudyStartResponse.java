package sh.egoeng.api.word.controller.dto.response;

import java.util.List;

/**
 * 벌크 학습 시작 응답
 */
public record BulkStudyStartResponse(
        /**
         * 학습 시작된 단어 개수
         */
        Integer count,
        /**
         * 학습 시작된 단어 ID 목록
         */
        List<Long> userWordIds
) {}













