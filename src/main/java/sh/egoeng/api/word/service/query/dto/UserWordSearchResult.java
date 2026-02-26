package sh.egoeng.api.word.service.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserWordSearchResult {
    private Long wordId;
    private String text;
    private String meaning;
    private Boolean isFavourite;
    private String example;

    public UserWordSearchResult(Long wordId, String text, String meaning, Boolean isFavourite, String example) {
        this.wordId = wordId;
        this.text = text;
        this.meaning = meaning;
        this.isFavourite = isFavourite;
        this.example = example;
    }
}
