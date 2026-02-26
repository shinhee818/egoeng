package sh.egoeng.feign.papago.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Papago API 응답
 */
@JsonIgnoreProperties(ignoreUnknown = true) // 불필요한 필드를 무시
public record PapagoTranslationResponse(
        Message message
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            Result result
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            String translatedText, // 번역된 최종 텍스트
            String srcLangType,
            String tarLangType
    ) {}
}