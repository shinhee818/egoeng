package sh.egoeng.feign.llm.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResult {
    private String message;           // AI 응답 메시지
    private String sessionId;         // 대화 세션 ID
    private Long timestamp;           // 응답 시간 (밀리초)
}
