package sh.egoeng.domain.word;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.egoeng.domain.BaseEntity;

@Entity
@Table(name = "user_word_tag",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_word_id", "tag_id"}),
       indexes = {
           @Index(name = "idx_user_word_tag_user_word", columnList = "user_word_id"),
           @Index(name = "idx_user_word_tag_tag", columnList = "tag_id")
       })
@Getter
@NoArgsConstructor
public class UserWordTag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_word_id", nullable = false)
    private UserWord userWord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private WordTag tag;

    @Builder
    public UserWordTag(UserWord userWord, WordTag tag) {
        this.userWord = userWord;
        this.tag = tag;
    }
}

















