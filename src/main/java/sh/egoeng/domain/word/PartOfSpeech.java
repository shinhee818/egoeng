package sh.egoeng.domain.word;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PartOfSpeech {

    NOUN("noun", "명사"),
    VERB("verb", "동사"),
    ADJECTIVE("adjective", "형용사"),
    ADVERB("adverb", "부사"),
    PREPOSITION("preposition", "전치사"),
    CONJUNCTION("conjunction", "접속사"),
    PRONOUN("pronoun", "대명사"),
    INTERJECTION("interjection", "감탄사"),
    ARTICLE("article", "관사");

    private final String code;
    private final String description;

    public static PartOfSpeech fromCode(String code) {
        for (PartOfSpeech pos : values()) {
            if (pos.code.equalsIgnoreCase(code)) {
                return pos;
            }
        }
        throw new IllegalArgumentException("Unknown part_of_speech: " + code);
    }
}
