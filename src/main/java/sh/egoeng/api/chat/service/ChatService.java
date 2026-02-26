package sh.egoeng.api.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sh.egoeng.api.chat.controller.dto.request.ChatMessageRequest;
import sh.egoeng.api.chat.controller.dto.response.ChatMessageResponse;
import sh.egoeng.api.chat.service.dto.ChatAskRequest;
import sh.egoeng.api.chat.service.dto.ChatAskResponse;
import sh.egoeng.api.chat.service.llm.LlmChatClientService;
import sh.egoeng.domain.chat.ChatMessage;
import sh.egoeng.domain.chat.ChatMessageRepository;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final LlmChatClientService llmChatClientService;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatMessageResponse chat(Long userId, ChatMessageRequest request) {
        ChatAskRequest askRequest = request.toServiceRequest(userId);
        
        ChatAskResponse askResponse = llmChatClientService.ask(askRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        ChatMessage chatMessage = ChatMessage.builder()
                .user(user)
                .message(request.message())
                .response(askResponse.response())
                .createdAt(LocalDateTime.now())
                .build();
        
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .message(chatMessage.getMessage())
                .response(chatMessage.getResponse())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

    public SseEmitter chatStream(Long userId, ChatMessageRequest request) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        ChatAskRequest askRequest = request.toServiceRequest(userId);
        
        // 비동기로 스트리밍 처리
        CompletableFuture.runAsync(() -> {
            try {
                llmChatClientService.streamChat(askRequest)
                        .subscribe(
                                chunk -> {
                                    try {
                                        emitter.send(SseEmitter.event()
                                                .name("message")
                                                .data(chunk));
                                    } catch (IOException e) {
                                        log.error("SSE send error", e);
                                        emitter.completeWithError(e);
                                    }
                                },
                                error -> {
                                    log.error("Stream error", error);
                                    emitter.completeWithError(error);
                                },
                                () -> {
                                    try {
                                        emitter.send(SseEmitter.event()
                                                .name("end")
                                                .data("Stream completed"));
                                        emitter.complete();
                                    } catch (IOException e) {
                                        log.error("SSE completion error", e);
                                        emitter.completeWithError(e);
                                    }
                                }
                        );
            } catch (Exception e) {
                log.error("Streaming initialization error", e);
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    public List<ChatMessageResponse> getHistory(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return messages.stream()
                .map(msg -> ChatMessageResponse.builder()
                        .id(msg.getId())
                        .message(msg.getMessage())
                        .response(msg.getResponse())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .toList();
    }
}
