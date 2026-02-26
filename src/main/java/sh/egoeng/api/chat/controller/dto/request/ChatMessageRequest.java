package sh.egoeng.api.chat.controller.dto.request;

import sh.egoeng.api.chat.service.dto.ChatAskRequest;

public record ChatMessageRequest(
        String message  // 사용자가 보낸 메시지
) {
    public ChatAskRequest toServiceRequest(Long userId) {
        return new ChatAskRequest(message, userId);
    }
}
