package sh.egoeng.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserWordTagRepository extends JpaRepository<UserWordTag, Long> {
    /**
     * UserWord에 연결된 모든 태그 조회
     */
    List<UserWordTag> findByUserWordId(Long userWordId);

    /**
     * UserWord에 연결된 태그 조회 (태그 정보 포함)
     */
    @Query("SELECT uwt FROM UserWordTag uwt JOIN FETCH uwt.tag WHERE uwt.userWord.id = :userWordId")
    List<UserWordTag> findByUserWordIdWithTag(@Param("userWordId") Long userWordId);

    /**
     * 특정 태그에 연결된 모든 UserWord 조회
     */
    @Query("SELECT uwt FROM UserWordTag uwt JOIN FETCH uwt.userWord WHERE uwt.tag.id = :tagId")
    List<UserWordTag> findByTagIdWithUserWord(@Param("tagId") Long tagId);

    /**
     * UserWord와 Tag의 연결 존재 여부 확인
     */
    boolean existsByUserWordIdAndTagId(Long userWordId, Long tagId);

    /**
     * UserWord와 Tag의 연결 조회
     */
    Optional<UserWordTag> findByUserWordIdAndTagId(Long userWordId, Long tagId);

    /**
     * UserWord의 모든 태그 연결 삭제
     */
    void deleteByUserWordId(Long userWordId);

    /**
     * 특정 태그의 모든 연결 삭제
     */
    void deleteByTagId(Long tagId);

    /**
     * 특정 태그에 연결된 UserWord 개수 조회
     */
    long countByTagId(Long tagId);

    /**
     * 여러 태그에 모두 연결된 UserWord 조회 (AND 조건)
     */
    @Query("""
        SELECT DISTINCT uwt.userWord 
        FROM UserWordTag uwt 
        WHERE uwt.tag.id IN :tagIds 
        GROUP BY uwt.userWord.id 
        HAVING COUNT(DISTINCT uwt.tag.id) = :tagCount
        """)
    List<sh.egoeng.domain.word.UserWord> findUserWordsByAllTags(
            @Param("tagIds") List<Long> tagIds, 
            @Param("tagCount") Long tagCount);

    /**
     * 여러 UserWord ID에 대한 태그 정보를 배치로 조회 (JOIN FETCH로 N+1 방지)
     */
    @Query("SELECT uwt FROM UserWordTag uwt JOIN FETCH uwt.tag WHERE uwt.userWord.id IN :userWordIds")
    List<UserWordTag> findByUserWordIdIn(@Param("userWordIds") List<Long> userWordIds);
}


