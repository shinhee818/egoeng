package sh.egoeng.domain.quiz;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;
import sh.egoeng.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name="user_quiz")
@Getter
@NoArgsConstructor
public class UserQuiz extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private Integer score;

    private Integer retry;

    @Builder
    public UserQuiz(User user, Quiz quiz, Integer score, Integer retry) {
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.retry = retry;
    }

    /**
     * 점수 업데이트
     * 퀴즈 답안 제출 후 정답 개수를 score로 저장
     */
    public void updateScore(Integer score) {
        this.score = score;
    }
}
