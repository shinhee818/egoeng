package sh.egoeng.api.word.service.query.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QueryWordSearchResult(
        Long id,
        String text,
        LocalDateTime createdAt,
        List<MeaningInfo> meanings
) {
    public record MeaningInfo(
            Long id,
            String meaningKo,
            String partOfSpeech,
            String example
    ) {}
}
