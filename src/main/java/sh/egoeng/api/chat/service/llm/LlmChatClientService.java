package sh.egoeng.api.chat.service.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import sh.egoeng.api.chat.service.dto.ChatAskRequest;
import sh.egoeng.api.chat.service.dto.ChatAskResponse;
import sh.egoeng.feign.llm.chat.LlmChatClient;
import sh.egoeng.feign.llm.chat.dto.request.ChatRequest;
import sh.egoeng.feign.llm.chat.dto.response.ChatResult;
import sh.egoeng.feign.llm.quiz.dto.response.LlmResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmChatClientService {
    private final LlmChatClient llmChatClient;
    private final WebClient webClient;
    
    @Value("${llm.chat-base-url}")
    private String llmBaseUrl;

    public ChatAskResponse ask(ChatAskRequest serviceRequest) {
        ChatRequest feignRequest = serviceRequest.toFeignRequest();

        LlmResponse<ChatResult> response = llmChatClient.chat(feignRequest);

        if (response == null || !response.isSuccess() || response.getResult() == null) {
            throw new RuntimeException("LLM 서버에서 채팅 응답 실패");
        }

        return new ChatAskResponse(response.getResult().getMessage());
    }

    public Flux<String> streamChat(ChatAskRequest serviceRequest) {
        ChatRequest feignRequest = serviceRequest.toFeignRequest();
        log.debug("Starting SSE stream for user: {}", feignRequest.getUserId());
        
        return webClient.post()
                .uri(llmBaseUrl + "/api/chat/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(feignRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(chunk -> log.debug("Received chunk: {}", chunk))
                .doOnError(error -> log.error("SSE streaming error", error))
                .doOnComplete(() -> log.debug("SSE streaming completed"));
    }
}
