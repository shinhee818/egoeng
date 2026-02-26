package sh.egoeng.api.word.controller.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record WordSearchResponse(
        List<WordSearchResult> words,
        long totalElements,
        int totalPages,
        @JsonProperty("currentPage")
        int currentPage,
        @JsonProperty("pageSize")
        int pageSize,
        @JsonProperty("hasNext")
        boolean hasNext,
        @JsonProperty("hasPrevious")
        boolean hasPrevious
) {
    public record WordSearchResult(
            Long id,
            String text,
            String meaningKo,
            String partOfSpeech,
            String difficulty,
            String exampleSentenceEn,
            String exampleSentenceKo,
            String pronunciation,
            @JsonProperty("popularityScore")
            Integer popularityScore,
            LocalDateTime createdAt,
            @JsonProperty("isUserAdded")
            Boolean isUserAdded
    ) {}
}

