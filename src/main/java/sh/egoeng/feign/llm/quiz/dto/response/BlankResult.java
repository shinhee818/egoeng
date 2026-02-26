package sh.egoeng.feign.llm.quiz.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlankResult extends QuizResult {
    private List<String> userAnswers;   // 사용자 답변 리스트 (응답에서 userAnswers로 옴)
    private String correctAnswer;        // 전체 정답 문장 (예: "She loves to explore new cities whenever she travels for work.")
    private List<String> correctWords;   // 각 빈칸의 정답 (예: ["cities", "work"])
    private List<String> llmAnswer;      // LLM이 추론한 정답
    private List<Boolean> perBlank;      // 각 빈칸별 정답 여부
}
