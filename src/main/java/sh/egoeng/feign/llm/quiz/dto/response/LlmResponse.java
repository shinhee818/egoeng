package sh.egoeng.feign.llm.quiz.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LlmResponse<T> {
    private boolean success;  // 처리 성공 여부
    private String type;      // 퀴즈 종류 (translate / blank / choice)
    private T result;         // 퀴즈별 세부 응답
}