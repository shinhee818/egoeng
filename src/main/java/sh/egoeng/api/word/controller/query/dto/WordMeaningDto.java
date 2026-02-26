package sh.egoeng.api.word.controller.query.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WordMeaningDto(
        Long id,
        String meaningKo,
        String partOfSpeech,
        String difficulty,
        String exampleSentenceEn,
        String exampleSentenceKo,
        String pronunciation,
        @JsonProperty("popularityScore")
        Integer popularityScore,
        @JsonProperty("createdAt")
        String createdAt,
        @JsonProperty("isUserAdded")
        Boolean isUserAdded
) {}
















