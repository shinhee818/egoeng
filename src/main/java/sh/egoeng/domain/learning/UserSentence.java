package sh.egoeng.domain.learning;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;
import sh.egoeng.domain.user.User;

@Entity
@Table(
    name = "user_sentence",
    indexes = {
        @Index(name = "idx_user_sentence_user_created", columnList = "user_id, created_at"),
        @Index(name = "idx_user_sentence_daily_sentence", columnList = "daily_sentence_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSentence extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자
     * 이 문장을 저장한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 오늘의 문장
     * 이 문장이 DailySentence에서 저장된 경우 참조
     * 직접 입력한 경우 null
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_sentence_id")
    private DailySentence dailySentence;

    /**
     * 영어 문장
     * 예: "How's it going?"
     */
    @Column(nullable = false, length = 1000)
    private String sentence;

    /**
     * 한국어 의미
     * 예: "어떻게 지내?"
     */
    @Column(name = "meaning_ko", nullable = false, length = 500)
    private String meaningKo;

    /**
     * 상세 해석
     * 문장의 사용 상황, 설명 등
     * 예: "친한 사이에서 사용하는 인사 표현입니다."
     */
    @Column(columnDefinition = "TEXT")
    private String explanation;

    /**
     * 카테고리
     * 예: "일상", "비즈니스", "여행" 등
     */
    @Column(length = 50)
    private String category;

    /**
     * 즐겨찾기 여부
     * 사용자가 중요한 문장을 즐겨찾기로 표시할 수 있음
     */
    @Column(name = "is_favourite")
    private Boolean isFavourite = false;

    /**
     * 학습 완료 시간
     * 이 문장을 학습 완료한 시간
     * null이면 아직 학습하지 않은 문장
     */
    @Column(name = "learned_at")
    private java.time.LocalDateTime learnedAt;

    /**
     * 학습 횟수
     * 이 문장을 몇 번 학습했는지 카운트
     * 학습 완료할 때마다 1씩 증가
     */
    @Column(name = "learning_count")
    private Integer learningCount = 0;

    @Builder
    public UserSentence(User user, DailySentence dailySentence, String sentence, String meaningKo, String explanation, String category, Boolean isFavourite, java.time.LocalDateTime learnedAt, Integer learningCount) {
        this.user = user;
        this.dailySentence = dailySentence;
        this.sentence = sentence;
        this.meaningKo = meaningKo;
        this.explanation = explanation;
        this.category = category;
        this.isFavourite = isFavourite != null ? isFavourite : false;
        this.learnedAt = learnedAt;
        this.learningCount = learningCount != null ? learningCount : 0;
    }

    /**
     * 즐겨찾기 토글
     * 즐겨찾기 상태를 반전시킴
     */
    public void toggleFavourite() {
        this.isFavourite = !this.isFavourite;
    }

    /**
     * 즐겨찾기 설정
     * @param isFavourite 즐겨찾기 여부
     */
    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    /**
     * 학습 완료 처리
     * 이 문장의 학습을 완료했을 때 호출
     * - learnedAt을 현재 시간으로 업데이트
     * - learningCount를 1 증가
     */
    public void completeLearning() {
        this.learnedAt = java.time.LocalDateTime.now();
        this.learningCount = (this.learningCount != null ? this.learningCount : 0) + 1;
    }
}

