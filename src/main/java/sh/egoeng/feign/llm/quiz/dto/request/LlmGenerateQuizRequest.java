package sh.egoeng.feign.llm.quiz.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LlmGenerateQuizRequest extends LlmRequest {
    private String quizType;

    @Builder
    public LlmGenerateQuizRequest(String quizType) {
        this.quizType = quizType;
        setType(quizType);
    }

    @Override
    public String buildPrompt() {
        return "Generate a " + quizType + " quiz question.";
    }
}



