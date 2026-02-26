package sh.egoeng.api.word.controller.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AutocompleteResponse(
        List<Suggestion> suggestions
) {
    public record Suggestion(
            String text,
            String meaningKo,
            @JsonProperty("popularity")
            Integer popularity
    ) {}
}

