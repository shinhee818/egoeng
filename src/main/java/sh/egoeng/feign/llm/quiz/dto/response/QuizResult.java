package sh.egoeng.feign.llm.quiz.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class QuizResult {
    private String prompt;
    private String userAnswer;
    private boolean correct;
    private String feedback;
}
