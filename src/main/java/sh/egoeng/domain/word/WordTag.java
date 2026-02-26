package sh.egoeng.domain.word;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;
import sh.egoeng.domain.user.User;

@Entity
@Table(name = "word_tag",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}),
       indexes = {
           @Index(name = "idx_word_tag_user_id", columnList = "user_id"),
           @Index(name = "idx_word_tag_name", columnList = "name")
       })
@Getter
@NoArgsConstructor
public class WordTag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;  // 태그 이름 (예: "비즈니스", "일상", "중요")

    @Column(length = 20)
    private String color;  // UI 표시용 색상 (예: "#FF5733")

    @Builder
    public WordTag(User user, String name, String color) {
        this.user = user;
        this.name = name;
        this.color = color;
    }
}

















