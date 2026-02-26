package sh.egoeng.api.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import sh.egoeng.api.chat.controller.dto.request.ChatMessageRequest;
import sh.egoeng.api.chat.controller.dto.response.ChatMessageResponse;
import sh.egoeng.api.chat.service.ChatService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/{userId}")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long userId,
            @RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(chatService.chat(userId, request));
    }

    @PostMapping("/stream/{userId}")
    public SseEmitter streamMessage(
            @PathVariable Long userId,
            @RequestBody ChatMessageRequest request) {
        return chatService.chatStream(userId, request);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getHistory(userId));
    }
}
