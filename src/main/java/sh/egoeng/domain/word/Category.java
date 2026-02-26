package sh.egoeng.domain.word;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "category", indexes = @Index(name = "idx_category_name", columnList = "name"))
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 카테고리 이름
     * 예: "일상", "비즈니스", "여행", "TOEIC", "TOEFL" 등
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 카테고리 설명
     * 예: "일상 생활에서 자주 사용하는 영어 단어"
     */
    @Column(length = 500)
    private String description;

    @Builder
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

