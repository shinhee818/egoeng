package sh.egoeng.api.word.service.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sh.egoeng.api.word.service.query.dto.UserWordSearchResult;
import sh.egoeng.domain.word.UserWord;

import java.util.List;

public interface UserWordQueryRepository extends JpaRepository<UserWord, Long> {

    @Query("""
            SELECT new sh.egoeng.api.word.service.query.dto.UserWordSearchResult(
                w.id,
                w.text,
                wm.meaningKo,
                uw.isFavourite,
                wm.example
            )
            FROM UserWord uw
            JOIN uw.word w
            LEFT JOIN WordMeaning wm ON wm.word = w
                AND wm.id = (
                    SELECT MIN(wm2.id)
                    FROM WordMeaning wm2
                    WHERE wm2.word = w
                )
            WHERE uw.user.id = :userId
            ORDER BY uw.id DESC
            """)
    List<UserWordSearchResult> search(@Param("userId") Long userId);

    @Query("""
            SELECT new sh.egoeng.api.word.service.query.dto.UserWordSearchResult(
                w.id,
                w.text,
                wm.meaningKo,
                uw.isFavourite,
                wm.example
            )
            FROM UserWord uw
            JOIN uw.word w
            LEFT JOIN WordMeaning wm ON wm.word = w
                AND wm.id = (
                    SELECT MIN(wm2.id)
                    FROM WordMeaning wm2
                    WHERE wm2.word = w
                )
            WHERE uw.user.id = :userId
            AND (LOWER(w.text) LIKE LOWER(CONCAT('%', :searchText, '%'))
                OR LOWER(wm.meaningKo) LIKE LOWER(CONCAT('%', :searchText, '%')))
            ORDER BY uw.id DESC
            """)
    List<UserWordSearchResult> searchByText(@Param("userId") Long userId, @Param("searchText") String searchText);

    @Query("""
            SELECT new sh.egoeng.api.word.service.query.dto.UserWordSearchResult(
                w.id,
                w.text,
                wm.meaningKo,
                uw.isFavourite,
                wm.example
            )
            FROM UserWord uw
            JOIN uw.word w
            LEFT JOIN WordMeaning wm ON wm.word = w
                AND wm.id = (
                    SELECT MIN(wm2.id)
                    FROM WordMeaning wm2
                    WHERE wm2.word = w
                )
            WHERE uw.user.id = :userId
            AND uw.isFavourite = true
            ORDER BY uw.id DESC
            """)
    List<UserWordSearchResult> searchFavorites(@Param("userId") Long userId);
}
