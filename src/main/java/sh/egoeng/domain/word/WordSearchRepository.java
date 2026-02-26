package sh.egoeng.domain.word;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sh.egoeng.domain.word.service.WordSearchDto;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;

import java.util.List;

import static sh.egoeng.domain.word.QWord.word;
import static sh.egoeng.domain.word.QWordMeaning.wordMeaning;

@Repository
@RequiredArgsConstructor
public class WordSearchRepository {

    private final JPAQueryFactory queryFactory;

    public Page<WordSearchDtoProjection> findWordsByQuery(String query, Pageable pageable) {
        // Step 1: 검색 조건을 만족하는 Word ID 목록을 페이징하여 가져오기
        List<Long> wordIds = queryFactory
                .select(word.id)
                .from(word)
                .where(buildSearchConditions(query))
                .orderBy(
                        word.text.length().asc(), // 짧은 단어 우선
                        word.createdAt.desc() // 최신순
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (wordIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // Step 2: 해당 Word ID들의 첫 번째 의미 조회
        List<WordSearchDto> content = queryFactory
                .select(Projections.constructor(
                        WordSearchDto.class,
                        word.id,
                        wordMeaning.meaningKo,
                        word.text,
                        word.createdAt,
                        word.id.multiply(0)))
                .from(word)
                .leftJoin(word.meanings, wordMeaning)
                .where(word.id.in(wordIds)
                        .and(wordMeaning.id.eq(
                                queryFactory.select(wordMeaning.id.min())
                                        .from(wordMeaning)
                                        .where(wordMeaning.word.id.eq(word.id))
                        )))
                .fetch();

        // Step 3: 전체 개수 쿼리
        Long total = queryFactory
                .select(word.count())
                .from(word)
                .where(buildSearchConditions(query))
                .fetchOne();

        // DTO를 인터페이스 타입으로 변환
        List<WordSearchDtoProjection> projectionContent = content.stream()
                .map(dto -> (WordSearchDtoProjection) dto)
                .toList();

        return new PageImpl<>(projectionContent, pageable, total != null ? total : 0);
    }

    private BooleanExpression buildSearchConditions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null; // 조건 없음 - 전체 조회
        }

        String trimmedQuery = query.trim();
        return word.text.containsIgnoreCase(trimmedQuery); // LIKE '%query%' 검색
    }
}


