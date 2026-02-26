package sh.egoeng.feign.llm.quiz.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class LlmRequest {
    private String type;
    private String context;
    private String prompt;

    public abstract String buildPrompt();
}
