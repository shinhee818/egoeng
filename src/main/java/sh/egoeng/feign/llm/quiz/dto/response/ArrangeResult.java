package sh.egoeng.feign.llm.quiz.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArrangeResult {
    private List<String> prompt;      // 섞인 단어들(LLM이 내려주는 경우)
    private String userAnswer;        // 사용자가 만든 문장
    private String correctAnswer;     // 정답 문장
    private boolean correct;          // 정답 여부
    private String feedback;          // 피드백
    private List<Boolean> perWord;    // 단어별 정답 여부(옵션)
}


















