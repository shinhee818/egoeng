package sh.egoeng.api.word.controller.dto.request;

/**
 * 개별 단어 학습 시작 요청
 */
public record StudyStartRequest(
        /**
         * 학습 목표 개수
         * 이 단어를 학습할 때 설정한 목표 개수
         * 예: "오늘 10개 단어 학습하기" 중 이 단어가 포함된 경우
         * 선택적 필드 (null 가능)
         */
        Integer learningGoal
) {}
