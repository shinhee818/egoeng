package sh.egoeng.domain.word;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "word_meaning")
public class WordMeaning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meaning_ko")
    private String meaningKo;

    @Column(name = "part_of_speech")
    private String partOfSpeech;

    @Column(nullable = true)
    private String example;

    private String sourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Builder
    public WordMeaning(String meaningKo, String sourceId) {
        this.meaningKo = meaningKo;
        this.sourceId = sourceId;
    }

    void setWord(Word word) {
        this.word = word;
    }
}
