package sh.egoeng.api.word.controller.dto.response;

import java.util.List;

public record CsvImportResponse(
        boolean success,
        int totalRows,
        int successCount,
        int failureCount,
        List<ImportResult> results
) {
    public record ImportResult(
            int row,
            String word,
            String status,  // SUCCESS, FAILURE
            Long userWordId,
            String message
    ) {}
}











