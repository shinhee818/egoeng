package sh.egoeng.feign.llm.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sh.egoeng.feign.llm.quiz.dto.request.LlmRequest;

@Getter
@Setter
public class ChatRequest extends LlmRequest {
    private String message;           // 사용자 메시지
    private Long userId;              // 사용자 ID (컨텍스트용)
    private String sessionId;         // 대화 세션 ID (선택)

    @Builder
    public ChatRequest(String message, Long userId, String sessionId) {
        this.message = message;
        this.userId = userId;
        this.sessionId = sessionId;
        setType("CHAT");
        setPrompt(buildPrompt());
    }

    @Override
    public String buildPrompt() {
        return "You are an English learning tutor. Answer the following question:\n" + message;
    }
}
