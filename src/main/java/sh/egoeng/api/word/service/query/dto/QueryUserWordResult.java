package sh.egoeng.api.word.service.query.dto;

import sh.egoeng.domain.word.LearningStatus;

import java.time.LocalDateTime;
import java.util.List;

public record QueryUserWordResult(
        Long id,  // Word ID (커스텀 단어일 경우 UserWord ID)
        Long userWordId,  // UserWord ID (삭제 등에 사용)
        String meaningKo,
        String text,
        LocalDateTime createdAt,
        List<TagInfo> tags,
        LearningStatus learningStatus
) {
    public record TagInfo(
            Long id,
            String name,
            String color
    ) {}
}




