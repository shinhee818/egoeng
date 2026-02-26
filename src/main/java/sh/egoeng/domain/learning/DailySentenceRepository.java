package sh.egoeng.domain.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailySentenceRepository extends JpaRepository<DailySentence, Long> {

    /**
     * 날짜로 오늘의 문장 조회
     * @param date 날짜
     * @return 해당 날짜의 DailySentence (없으면 Optional.empty())
     */
    Optional<DailySentence> findByDate(LocalDate date);

    /**
     * 랜덤으로 오늘의 문장 조회
     * @return 랜덤하게 선택된 DailySentence (없으면 Optional.empty())
     */
    @Query(value = "SELECT * FROM daily_sentence ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<DailySentence> findRandom();
}

