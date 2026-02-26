package sh.egoeng.domain.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryWordRepository extends JpaRepository<CategoryWord, Long> {

    /**
     * 카테고리 ID로 조회
     * @param categoryId 카테고리 ID
     * @return CategoryWord 목록
     */
    List<CategoryWord> findByCategoryId(Long categoryId);

    /**
     * 단어 ID로 조회
     * @param wordId 단어 ID
     * @return CategoryWord 목록
     */
    List<CategoryWord> findByWordId(Long wordId);

    /**
     * 카테고리 ID와 단어 ID로 조회
     * @param categoryId 카테고리 ID
     * @param wordId 단어 ID
     * @return CategoryWord (없으면 Optional.empty())
     */
    @Query("SELECT cw FROM CategoryWord cw WHERE cw.category.id = :categoryId AND cw.word.id = :wordId")
    java.util.Optional<CategoryWord> findByCategoryIdAndWordId(@Param("categoryId") Long categoryId, @Param("wordId") Long wordId);

    /**
     * 카테고리 ID와 단어 ID 존재 여부 확인
     * @param categoryId 카테고리 ID
     * @param wordId 단어 ID
     * @return 존재 여부
     */
    boolean existsByCategoryIdAndWordId(Long categoryId, Long wordId);
}













