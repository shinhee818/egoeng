package sh.egoeng.feign.llm.quiz.dto.request;

import lombok.Getter;
import lombok.Setter;
import sh.egoeng.feign.llm.quiz.dto.QuestionType;

@Getter
@Setter
public abstract class QuizRequest extends LlmRequest {
    private QuestionType questionType;
    public abstract String buildPrompt();
}
