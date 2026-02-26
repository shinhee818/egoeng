package sh.egoeng.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordMeaningRepository extends JpaRepository<WordMeaning, Long> {
    List<WordMeaning> findAllByWordIn(List<Word> words);
    List<WordMeaning> findAllByWordIdIn(List<Long> wordIds);
}