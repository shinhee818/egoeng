package sh.egoeng.api.chat.service.dto;

import sh.egoeng.feign.llm.chat.dto.request.ChatRequest;

public record ChatAskRequest(
        String message,
        Long userId
) {
    public  ChatRequest toFeignRequest() {
        return ChatRequest.builder()
                .message(message)
                .userId(userId)
                .build();
    }
}
