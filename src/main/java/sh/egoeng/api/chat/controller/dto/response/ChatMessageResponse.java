package sh.egoeng.api.chat.controller.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponse(
        Long id,
        String message,     // 사용자 메시지
        String response,    // AI 응답
        LocalDateTime createdAt
) {
}
