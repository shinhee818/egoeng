package sh.egoeng.domain.learning;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sh.egoeng.domain.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSentenceRepository extends JpaRepository<UserSentence, Long> {

    /**
     * 사용자별 문장 목록 조회 (최신순)
     * @param user 사용자
     * @param pageable 페이지 정보
     * @return 사용자의 문장 목록
     */
    Page<UserSentence> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * 사용자별 즐겨찾기 문장 목록 조회
     * @param user 사용자
     * @return 즐겨찾기 문장 목록
     */
    List<UserSentence> findByUserAndIsFavouriteTrueOrderByCreatedAtDesc(User user);

    /**
     * 사용자와 DailySentence로 문장 조회
     * 같은 오늘의 문장을 중복 저장하지 않기 위해 사용
     * @param user 사용자
     * @param dailySentence 오늘의 문장
     * @return UserSentence (없으면 Optional.empty())
     */
    Optional<UserSentence> findByUserAndDailySentence(User user, DailySentence dailySentence);

    /**
     * 사용자 ID로 문장 개수 조회
     * @param userId 사용자 ID
     * @return 문장 개수
     */
    @Query("SELECT COUNT(us) FROM UserSentence us WHERE us.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}














