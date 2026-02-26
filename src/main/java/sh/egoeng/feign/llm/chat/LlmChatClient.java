package sh.egoeng.feign.llm.chat;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import sh.egoeng.feign.llm.chat.dto.request.ChatRequest;
import sh.egoeng.feign.llm.chat.dto.response.ChatResult;
import sh.egoeng.feign.llm.quiz.dto.response.LlmResponse;

@FeignClient(name = "chatClient", url = "${llm.chat-base-url}")
public interface LlmChatClient {
    @PostMapping("/api/chat")
    LlmResponse<ChatResult> chat(@RequestBody ChatRequest request);
}
