package sh.egoeng.dsl;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WordBody {
    private String question;
    private List<OptionItem> options;
    private int correctOptionId;
    private List<Long> refWordIds;

    @Getter
    @Builder
    public static class OptionItem {
        private int id;
        private String text;
    }
}
