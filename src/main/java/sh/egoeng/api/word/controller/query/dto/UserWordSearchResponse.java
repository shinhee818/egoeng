package sh.egoeng.api.word.controller.query.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserWordSearchResponse(
    List<WordSearchResult> words,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize,
    boolean hasNext,
    boolean hasPrevious
) {
    public record WordSearchResult(
            Long id,  // Word ID (커스텀 단어일 경우 UserWord ID)
            Long userWordId,  // UserWord ID (삭제 등에 사용)
            String meaningKo,
            String text,
            LocalDateTime createdAt,
            List<TagInfo> tags,
            String learningStatus
    ) {}
}
