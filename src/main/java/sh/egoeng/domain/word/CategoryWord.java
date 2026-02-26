package sh.egoeng.domain.word;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;

@Entity
@Table(
        name = "category_word",
        indexes = {
                @Index(name = "idx_category_word_category_id", columnList = "category_id"),
                @Index(name = "idx_category_word_word_id", columnList = "word_id"),
                @Index(name = "idx_category_word_category_word", columnList = "category_id, word_id", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryWord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * 단어
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Builder
    public CategoryWord(Category category, Word word) {
        this.category = category;
        this.word = word;
    }
}













