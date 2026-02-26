package sh.egoeng.domain.word;

import java.time.LocalDateTime;

public record WordSearchDto(
        Long id,
        String meaningKo,
        String text,
        LocalDateTime createdAt
) {}
