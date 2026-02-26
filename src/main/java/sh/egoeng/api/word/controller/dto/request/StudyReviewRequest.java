package sh.egoeng.api.word.controller.dto.request;

public record StudyReviewRequest(
        Boolean isCorrect,  // 정답 여부
        Integer difficulty  // 난이도 평가 (1-5, optional)
) {}
















