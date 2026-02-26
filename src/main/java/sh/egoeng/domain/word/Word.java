package sh.egoeng.domain.word;

import jakarta.persistence.*;
import lombok.*;
import sh.egoeng.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(exclude = {"meanings"})
@Table(name = "word", indexes = {
        @Index(name = "idx_word_text", columnList = "text"),
        @Index(name = "idx_word_level", columnList = "level")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_word_text", columnNames = "text")
})
public class Word extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512, unique = true)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_of_speech")
    private PartOfSpeech partOfSpeech;

    /**
     * 단어 난이도 레벨
     * BEGINNER: 초급
     * INTERMEDIATE: 중급
     * ADVANCED: 고급
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private WordLevel level;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WordMeaning> meanings = new ArrayList<>();

    @Column(name = "document_vector", insertable = false, updatable = false)
    private String ftsVector;


    @Builder
    public Word(String text) {
        this.text = text;
    }

    public void addMeanings(List<WordMeaning> meanings) {
        if (meanings == null) return;

        for (WordMeaning m : meanings) {
            m.setWord(this);
            this.meanings.add(m);
        }
    }

}