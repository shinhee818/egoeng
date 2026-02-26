package sh.egoeng.domain.word;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserWordRepository extends JpaRepository<UserWord, Long> {
    boolean existsUserWordByUserAndWord(User user, Word word);

    Optional<UserWord> findUserWordByUserAndWord(User user, Word word);
    
    long countByUserId(Long userId);

    // 커스텀 단어 중복 체크
    boolean existsByUserAndCustomTextAndCustomMeaningKo(User user, String customText, String customMeaningKo);

    @Query("SELECT uw FROM UserWord uw WHERE uw.user.id = :userId AND uw.word.id = :wordId")
    Optional<UserWord> findByUserIdAndWordId(@Param("userId") Long userId, @Param("wordId") Long wordId);

    @Query("select u from UserWord u join fetch u.word order by u.createdAt desc limit 10")
    List<UserWord> findLatestUserWords();

    @Query(value = """
        SELECT 
               COALESCE(w.id, uw.id) as id,
               COALESCE(m.meaning_ko, uw.custom_meaning_ko) as meaningKo,
               COALESCE(w.text, uw.custom_text) as text,
               uw.created_at as createdAt,
               uw.id as userWordId
        FROM user_word uw
        LEFT JOIN word w ON uw.word_id = w.id
        LEFT JOIN LATERAL (
            SELECT meaning_ko 
            FROM word_meaning 
            WHERE word_id = w.id 
            ORDER BY id
            LIMIT 1
        ) m ON true
        ORDER BY uw.created_at DESC
        LIMIT 10
        """,
        nativeQuery = true)
    List<WordSearchDtoProjection> findLatestUserWordsWithMeanings();

    // 오늘 복습할 단어 조회 (태그 필터링 없음)
    @Query("""
        SELECT uw FROM UserWord uw
        JOIN FETCH uw.word
        WHERE uw.user.id = :userId
          AND uw.learningStatus IN (:statuses)
          AND uw.nextReviewDate <= :today
        ORDER BY uw.nextReviewDate ASC, uw.createdAt DESC
        """)
    Page<UserWord> findTodayReviewWords(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            @Param("statuses") List<LearningStatus> statuses,
            Pageable pageable);

    // 오늘 복습할 단어 조회 (태그 필터링 있음)
    @Query("""
        SELECT DISTINCT uw FROM UserWord uw
        JOIN FETCH uw.word
        JOIN UserWordTag uwt ON uwt.userWord.id = uw.id
        JOIN WordTag wt ON uwt.tag.id = wt.id
        WHERE uw.user.id = :userId
          AND uw.learningStatus IN (:statuses)
          AND uw.nextReviewDate <= :today
          AND wt.id IN (:tagIds)
          AND wt.user.id = :userId
        GROUP BY uw.id
        HAVING COUNT(DISTINCT wt.id) = :tagIdsCount
        ORDER BY uw.nextReviewDate ASC, uw.createdAt DESC
        """)
    Page<UserWord> findTodayReviewWordsWithTags(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            @Param("statuses") List<LearningStatus> statuses,
            @Param("tagIds") List<Long> tagIds,
            @Param("tagIdsCount") int tagIdsCount,
            Pageable pageable);

    // 지연된 복습 단어 조회 (태그 필터링 없음)
    @Query("""
        SELECT uw FROM UserWord uw
        JOIN FETCH uw.word
        WHERE uw.user.id = :userId
          AND uw.learningStatus IN (:statuses)
          AND uw.nextReviewDate < :today
        ORDER BY uw.nextReviewDate ASC, uw.createdAt DESC
        """)
    Page<UserWord> findOverdueReviewWords(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            @Param("statuses") List<LearningStatus> statuses,
            Pageable pageable);

    // 지연된 복습 단어 조회 (태그 필터링 있음)
    @Query("""
        SELECT DISTINCT uw FROM UserWord uw
        JOIN FETCH uw.word
        JOIN UserWordTag uwt ON uwt.userWord.id = uw.id
        JOIN WordTag wt ON uwt.tag.id = wt.id
        WHERE uw.user.id = :userId
          AND uw.learningStatus IN (:statuses)
          AND uw.nextReviewDate < :today
          AND wt.id IN (:tagIds)
          AND wt.user.id = :userId
        GROUP BY uw.id
        HAVING COUNT(DISTINCT wt.id) = :tagIdsCount
        ORDER BY uw.nextReviewDate ASC, uw.createdAt DESC
        """)
    Page<UserWord> findOverdueReviewWordsWithTags(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            @Param("statuses") List<LearningStatus> statuses,
            @Param("tagIds") List<Long> tagIds,
            @Param("tagIdsCount") int tagIdsCount,
            Pageable pageable);

    // 최근 등록한 NEW 상태 단어 조회 (fromDateTime 포함)
    @Query(value = """
        SELECT uw.* FROM user_word uw
        INNER JOIN word w ON uw.word_id = w.id
        WHERE uw.user_id = :userId
          AND uw.learning_status = CAST(:status AS varchar)
          AND DATE(uw.created_at) >= :fromDate
        ORDER BY uw.created_at DESC
        LIMIT :limit
        """,
        nativeQuery = true)
    List<UserWord> findByUserIdAndLearningStatusOrderByCreatedAtDescWithDate(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("fromDate") LocalDate fromDate,
            @Param("limit") int limit);

    // JPQL 버전 (fromDate 없이)
    @Query("""
        SELECT uw FROM UserWord uw
        JOIN FETCH uw.word
        WHERE uw.user.id = :userId
          AND uw.learningStatus = :status
        ORDER BY uw.createdAt DESC
        """)
    List<UserWord> findByUserIdAndLearningStatusOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("status") LearningStatus status,
            org.springframework.data.domain.Pageable pageable);

    // Cursor 기반 학습 단어 조회 (인피니트 스크롤) - statuses 필터링 없음
    @Query("""
        SELECT uw FROM UserWord uw
        JOIN FETCH uw.word
        WHERE uw.user.id = :userId
          AND (:cursor IS NULL OR uw.id < :cursor)
        ORDER BY uw.id DESC
        """)
    List<UserWord> findByUserIdAndCursor(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            org.springframework.data.domain.Pageable pageable);

    // Cursor 기반 학습 단어 조회 (인피니트 스크롤) - statuses 필터링 있음
    @Query("""
        SELECT uw FROM UserWord uw
        JOIN FETCH uw.word
        WHERE uw.user.id = :userId
          AND (:cursor IS NULL OR uw.id < :cursor)
          AND uw.learningStatus IN :statuses
        ORDER BY uw.id DESC
        """)
    List<UserWord> findByUserIdAndCursorAndStatuses(
            @Param("userId") Long userId,
            @Param("cursor") Long cursor,
            @Param("statuses") List<LearningStatus> statuses,
            org.springframework.data.domain.Pageable pageable);

    // ID 리스트로 UserWord 조회 (word 포함)
    @Query("""
        SELECT uw FROM UserWord uw
        JOIN FETCH uw.word
        WHERE uw.id IN :ids
        ORDER BY uw.createdAt DESC
        """)
    List<UserWord> findAllByIdWithWord(@Param("ids") List<Long> ids);

    // 유저의 모든 단어 조회 (word 포함, 학습 시작용)
    @Query("""
        SELECT uw FROM UserWord uw
        JOIN FETCH uw.word
        WHERE uw.user.id = :userId
        """)
    List<UserWord> findAllByUserIdWithWord(@Param("userId") Long userId);

    // 오늘 등록한 단어 개수
    @Query(value = """
        SELECT COUNT(*) FROM user_word uw
        WHERE uw.user_id = :userId
        AND DATE(uw.created_at) = CURRENT_DATE
        """, nativeQuery = true)
    long countByUserIdAndCreatedAtIsToday(@Param("userId") Long userId);

}
