package sh.egoeng.api.word.controller.dto.request;

import java.util.List;

/**
 * 벌크 학습 시작 요청
 * 여러 단어를 한 번에 학습 시작할 때 사용
 */
public record BulkStudyStartRequest(
        /**
         * 학습 시작할 단어 ID 목록
         */
        List<Long> userWordIds,
        /**
         * 학습 목표 개수
         * 이 학습 세션에서 학습할 목표 단어 개수
         * 예: "오늘 10개 단어 학습하기" → 10
         * 선택적 필드 (null 가능)
         */
        Integer learningGoal
) {}













