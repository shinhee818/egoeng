package sh.egoeng.dsl;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TranslateBody {
    private String koreanSentence;
    private List<String> constraints;
    private List<String> answers;
}
