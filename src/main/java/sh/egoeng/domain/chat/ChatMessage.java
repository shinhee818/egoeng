package sh.egoeng.domain.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.user.User;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String message;     // 사용자 메시지
    
    @Column(columnDefinition = "TEXT")
    private String response;    // AI 응답

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(User user, String message, String response, LocalDateTime createdAt) {
        this.user = user;
        this.message = message;
        this.response = response;
        this.createdAt = createdAt;
    }
}
