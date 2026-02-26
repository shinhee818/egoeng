package sh.egoeng.dsl;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuizMeta {
    private String difficulty;
    private List<String> tags;
    private String note;
}
