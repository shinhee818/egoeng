package sh.egoeng.api.word.service.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sh.egoeng.api.word.service.query.dto.UserWordSearchResult;
import sh.egoeng.domain.word.QWordMeaning;

import java.util.List;

import static sh.egoeng.domain.word.QUserWord.userWord;

@Repository
@RequiredArgsConstructor
public class UserWordQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public List<UserWordSearchResult> search(Long userId) {
        QWordMeaning wordMeaning = QWordMeaning.wordMeaning;
        
        return queryFactory
                .select(Projections.constructor(
                        UserWordSearchResult.class,
                        userWord.word.id,
                        userWord.word.text,
                        wordMeaning.meaningKo,
                        userWord.isFavourite,
                        wordMeaning.example))
                .from(userWord)
                .join(userWord.word)
                .leftJoin(wordMeaning).on(wordMeaning.word.eq(userWord.word))
                .where(userWord.user.id.eq(userId))
                .orderBy(userWord.id.desc(), wordMeaning.id.asc()) // 첫 번째 의미 우선
                .distinct()
                .fetch();
    }    public List<UserWordSearchResult> searchByText(Long userId, String searchText) {
    QWordMeaning wordMeaning = QWordMeaning.wordMeaning;

    return queryFactory
        .select(Projections.constructor(
            UserWordSearchResult.class,
            userWord.word.id,
            userWord.word.text,
            wordMeaning.meaningKo,
            userWord.isFavourite,
            wordMeaning.example))
        .from(userWord)
        .join(userWord.word)
        .leftJoin(wordMeaning)
        .on(wordMeaning.word.eq(userWord.word)
            .and(wordMeaning.id.eq(
                JPAExpressions
                    .select(wordMeaning.id.min())
                    .from(QWordMeaning.wordMeaning)
                    .where(QWordMeaning.wordMeaning.word.eq(userWord.word)))))
        .where(userWord.user.id.eq(userId)
            .and(userWord.word.text.containsIgnoreCase(searchText)
                .or(wordMeaning.meaningKo.containsIgnoreCase(searchText))))
        .orderBy(userWord.id.desc())
        .fetch();
    }

    public List<UserWordSearchResult> searchFavorites(Long userId) {
    QWordMeaning wordMeaning = QWordMeaning.wordMeaning;

    return queryFactory
        .select(Projections.constructor(
            UserWordSearchResult.class,
            userWord.word.id,
            userWord.word.text,
            wordMeaning.meaningKo,
            userWord.isFavourite,
            wordMeaning.example))
        .from(userWord)
        .join(userWord.word)
        .leftJoin(wordMeaning)
        .on(wordMeaning.word.eq(userWord.word)
            .and(wordMeaning.id.eq(
                JPAExpressions
                    .select(wordMeaning.id.min())
                    .from(QWordMeaning.wordMeaning)
                    .where(QWordMeaning.wordMeaning.word.eq(userWord.word)))))
        .where(userWord.user.id.eq(userId)
            .and(userWord.isFavourite.isTrue()))
        .orderBy(userWord.id.desc())
        .fetch();
    }
}
