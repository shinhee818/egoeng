package sh.egoeng.domain.quiz;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private QuizType type;

    // JSONB DSL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> question;

    @Column
    private String title;

    @Column
    private String level;

    @Builder
    public Quiz(QuizType type, String title, Map<String, Object> question) {
        this.type = type;
        this.title = title;
        this.question = question;
    }

    @Column(name = "reg_dt")
    private LocalDateTime regDt;
}
