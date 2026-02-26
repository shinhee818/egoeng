package sh.egoeng.api.word.controller.query.dto;

import java.time.LocalDateTime;

public record TagListResponse(
        Long id,
        String name,
        String color,
        LocalDateTime createdAt,
        Long wordCount  // 이 태그가 연결된 단어 개수
) {}
















