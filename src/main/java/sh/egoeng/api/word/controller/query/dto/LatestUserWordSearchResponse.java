package sh.egoeng.api.word.controller.query.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LatestUserWordSearchResponse(
        Long id,  // Word ID (커스텀 단어일 경우 UserWord ID)
        Long userWordId,  // UserWord ID (삭제 등에 사용)
        String meaningKo,
        String text,
        LocalDateTime createdAt,
        List<TagInfo> tags,
        String learningStatus
) {}