package sh.egoeng.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordTagRepository extends JpaRepository<WordTag, Long> {
    /**
     * 사용자별 태그 조회 (생성일 기준 최신순)
     */
    List<WordTag> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 사용자와 태그 이름으로 태그 조회
     */
    Optional<WordTag> findByUserIdAndName(Long userId, String name);

    /**
     * 사용자별 태그 존재 여부 확인
     */
    boolean existsByUserIdAndName(Long userId, String name);

    /**
     * 사용자별 태그 개수 조회
     */
    long countByUserId(Long userId);

    /**
     * 사용자별 인기 태그 조회 (생성일 기준 최신순)
     */
    @Query("SELECT wt FROM WordTag wt WHERE wt.user.id = :userId ORDER BY wt.createdAt DESC")
    List<WordTag> findPopularTagsByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
}

