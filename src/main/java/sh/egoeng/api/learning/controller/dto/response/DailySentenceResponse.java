package sh.egoeng.api.learning.controller.dto.response;

import java.time.LocalDate;

public record DailySentenceResponse(
        Long id,
        String sentence,
        String meaningKo,
        String explanation,
        String category,
        String exampleDialogue,
        LocalDate date
) {
}














