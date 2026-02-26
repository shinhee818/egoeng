package sh.egoeng.domain.learning;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(
    name = "daily_sentence",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_daily_sentence_date", columnNames = "date")
    },
    indexes = {
        @Index(name = "idx_daily_sentence_date", columnList = "date")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySentence extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
     * 예시 대화
     * 문장이 사용된 예시 대화
     */
    @Column(name = "example_dialogue", columnDefinition = "TEXT")
    private String exampleDialogue;

    /**
     * 날짜
     * 이 문장이 제공된 날짜
     * 날짜별로 유일해야 함 (unique constraint)
     */
    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Builder
    public DailySentence(String sentence, String meaningKo, String explanation, String category, String exampleDialogue, LocalDate date) {
        this.sentence = sentence;
        this.meaningKo = meaningKo;
        this.explanation = explanation;
        this.category = category;
        this.exampleDialogue = exampleDialogue;
        this.date = date;
    }
}














