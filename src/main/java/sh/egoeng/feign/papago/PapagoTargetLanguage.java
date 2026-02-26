package sh.egoeng.feign.papago;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

public enum PapagoTargetLanguage {
    KOREAN("ko", "한국어"),
    ENGLISH("en", "영어"),
    JAPANESE("ja", "일본어"),
    ;

    private final String code;
    private final String displayName;

    PapagoTargetLanguage(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static PapagoTargetLanguage fromCode(String code) {
        return Stream.of(PapagoTargetLanguage.values())
                .filter(lang -> lang.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 대상 언어 코드입니다: " + code));
    }
}