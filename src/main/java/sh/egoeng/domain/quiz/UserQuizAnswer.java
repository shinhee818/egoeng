package sh.egoeng.domain.quiz;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sh.egoeng.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class UserQuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String answer;
    @Column(name = "is_correct")
    private boolean isCorrect;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "per_blank_correct")
    private List<Boolean> perBlankCorrect;  // 빈칸 퀴즈의 경우 각 빈칸별 정답 여부

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    @Builder
    public UserQuizAnswer(Quiz quiz, User user, String answer, boolean isCorrect, 
                          List<Boolean> perBlankCorrect,
                          LocalDateTime answeredAt, LocalDateTime regDt) {
        this.quiz = quiz;
        this.user = user;
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.perBlankCorrect = perBlankCorrect;
        this.answeredAt = answeredAt;
        this.regDt = regDt;
    }
}
