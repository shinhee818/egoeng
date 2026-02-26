package sh.egoeng.feign.llm.quiz.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChoiceResult extends QuizResult {
    private List<String> options;   // 보기 목록
    private String correctAnswer;   // 정답
    private String selectedOption;  // 사용자가 고른 답
}
