package sh.egoeng.dsl;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizDsl {
    private String type;
    private String version;
    private QuizMeta meta;
    private Object body;
}
