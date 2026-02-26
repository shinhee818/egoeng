package sh.egoeng.dsl;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BlankBody {
    private String koreanHint;
    private String sentence;
    private List<BlankItem> blanks;

    @Getter
    @Builder
    public static class BlankItem {
        private int index;
        private List<String> answer;
        private String explanation;
    }
}
