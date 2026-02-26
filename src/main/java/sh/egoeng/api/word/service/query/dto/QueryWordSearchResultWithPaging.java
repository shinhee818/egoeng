package sh.egoeng.api.word.service.query.dto;

import java.util.List;

public record QueryWordSearchResultWithPaging(
        List<QueryWordSearchResult> words,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {}

















