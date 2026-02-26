package sh.egoeng.domain.word;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sh.egoeng.domain.word.service.UserWordSearchDto;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static sh.egoeng.domain.word.QUserWord.userWord;
import static sh.egoeng.domain.word.QWord.word;
import static sh.egoeng.domain.word.QWordMeaning.wordMeaning;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserWordSearchRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 태그 필터링 없는 사용자 단어 검색 (간단 버전)
     * Word 테이블의 단어와 UserWord의 커스텀 단어를 모두 조회
     */
    public Page<WordSearchDtoProjection> findUserWordsWithoutTags(
            Long userId,
            String query,
            LocalDate fromDate,
            LocalDate toDate,
            LearningStatus learningStatus,
            Pageable pageable) {

        // UserWord 기본 조건 생성
        BooleanExpression baseCondition = buildBaseConditions(userId, query, fromDate, toDate);

        log.info("🔍 검색 시작: userId={}, query={}, learningStatus={}", userId, query, learningStatus);

        // 학습 상태 필터
        if (learningStatus != null) {
            baseCondition = baseCondition.and(userWord.learningStatus.eq(learningStatus));
            log.info("✅ learningStatus 필터 적용: {}", learningStatus);
        }

        // 먼저 UserWord IDs만 조회 (중복 제거)
        // LEFT JOIN 사용하여 커스텀 단어(word가 null)도 포함
        List<Long> userWordIds = queryFactory
                .select(userWord.id)
                .from(userWord)
                .leftJoin(userWord.word, word)
                .leftJoin(word.meanings, wordMeaning)
                .where(baseCondition)
                .groupBy(word.id, word.text, word.createdAt, userWord.id, userWord.createdAt)
                .fetch(); // GROUP BY로 중복 제거

        log.info("📊 조회된 UserWord ID 개수: {}", userWordIds.size());

        if (userWordIds.isEmpty()) {
            log.warn("⚠️ 조건에 맞는 데이터 없음");
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // Step 1: UserWord와 Word 정보만 조회
        List<Tuple> userWordWithWord = queryFactory
                .select(
                        userWord.id,
                        userWord.word.id,
                        userWord.customMeaningKo,
                        userWord.customText,
                        userWord.word.text,
                        userWord.createdAt,
                        userWord.wordType
                )
                .from(userWord)
                .leftJoin(userWord.word, word)
                .where(userWord.id.in(userWordIds))
                .orderBy(userWord.createdAt.desc())
                .fetch();

        // Step 2: Word ID별로 첫 번째 의미 조회
        List<Long> wordIds = userWordWithWord.stream()
                .map(tuple -> tuple.get(1, Long.class))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        log.info("🔎 [첫번째 메서드] 조회할 Word ID 목록: {}", wordIds);

        Map<Long, String> meaningByWordId = new java.util.HashMap<>();
        if (!wordIds.isEmpty()) {
            List<Tuple> meanings = queryFactory
                    .select(wordMeaning.word.id, wordMeaning.meaningKo)
                    .from(wordMeaning)
                    .where(wordMeaning.word.id.in(wordIds))
                    .orderBy(wordMeaning.id.asc())
                    .fetch();

            log.info("📚 [첫번째 메서드] 조회된 word_meaning 개수: {}", meanings.size());

            // 각 word별 첫 번째 의미만 저장
            meanings.forEach(tuple -> {
                Long wId = tuple.get(0, Long.class);
                String meaning = tuple.get(1, String.class);
                log.info("💾 [첫번째 메서드] Word ID {} -> 의미: {}", wId, meaning);
                meaningByWordId.putIfAbsent(wId, meaning);
            });
        }

        // Step 3: DTO 생성
        List<UserWordSearchDto> dtoList = userWordWithWord.stream()
                .map(tuple -> {
                    Long userWordId = tuple.get(0, Long.class);
                    Long wordId = tuple.get(1, Long.class);
                    String customMeaning = tuple.get(2, String.class);
                    String customText = tuple.get(3, String.class);
                    String wordText = tuple.get(4, String.class);
                    java.time.LocalDateTime createdAt = tuple.get(5, java.time.LocalDateTime.class);
                    WordType wordType = tuple.get(6, WordType.class);

                    String meaningKo = customMeaning != null ? customMeaning : meaningByWordId.get(wordId);
                    String text = wordText != null ? wordText : customText;

                    log.info("🏷️ [첫번째 메서드] DTO 생성: text={}, meaningKo={}, wordId={}", text, meaningKo, wordId);

                    return new UserWordSearchDto(
                            wordId != null ? wordId : userWordId,
                            meaningKo,
                            text,
                            createdAt,
                            userWordId,
                            wordType
                    );
                })
                .toList();

        log.info("✅ [첫번째 메서드] 조회된 결과 개수: {}", dtoList.size());

        // 전체 개수 쿼리
        // LEFT JOIN 사용하여 커스텀 단어(word가 null)도 포함
        Long total = queryFactory
                .select(userWord.count())
                .from(userWord)
                .leftJoin(userWord.word, word)
                .where(buildBaseConditions(userId, query, fromDate, toDate))
                .fetchOne();

        // DTO를 인터페이스 타입으로 변환
        List<WordSearchDtoProjection> projectionContent = dtoList.stream()
                .map(dto -> (WordSearchDtoProjection) dto)
                .toList();

        return new PageImpl<>(projectionContent, pageable, total != null ? total : 0);
    }

    /**
     * 태그 필터링 있는 사용자 단어 검색 (간단 버전)
     * Word 테이블의 단어와 UserWord의 커스텀 단어를 모두 조회
     */
    public Page<WordSearchDtoProjection> findUserWordsWithTags(
            Long userId,
            String query,
            LocalDate fromDate,
            LocalDate toDate,
            List<Long> tagIds,
            LearningStatus learningStatus,
            Pageable pageable) {

        if (tagIds == null || tagIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 태그가 있는 UserWord들의 ID를 먼저 조회 (간단한 방법)
        List<Long> userWordIdsWithTags = queryFactory
                .select(userWord.id)
                .from(userWord)
                .join(QUserWordTag.userWordTag).on(QUserWordTag.userWordTag.userWord.eq(userWord))
                .join(QWordTag.wordTag).on(QWordTag.wordTag.eq(QUserWordTag.userWordTag.tag))
                .where(userWord.user.id.eq(userId)
                        .and(QWordTag.wordTag.id.in(tagIds))
                        .and(buildDateAndQueryConditions(query, fromDate, toDate))
                        .and(learningStatus != null ? userWord.learningStatus.eq(learningStatus) : null))
                .groupBy(userWord.id)
                .having(QWordTag.wordTag.id.countDistinct().eq((long) tagIds.size()))
                .fetch();

        if (userWordIdsWithTags.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 페이징을 위해 먼저 ID만 추출
        List<Long> pagedUserWordIds = userWordIdsWithTags.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();

        if (pagedUserWordIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, userWordIdsWithTags.size());
        }

        // Step 1: UserWord와 Word 정보만 조회
        List<Tuple> userWordWithWord = queryFactory
                .select(
                        userWord.id,
                        userWord.word.id,
                        userWord.customMeaningKo,
                        userWord.customText,
                        userWord.word.text,
                        userWord.createdAt,
                        userWord.wordType
                )
                .from(userWord)
                .leftJoin(userWord.word, word)
                .where(userWord.id.in(pagedUserWordIds))
                .orderBy(userWord.createdAt.desc())
                .fetch();

        // Step 2: Word ID별로 첫 번째 의미 조회
        List<Long> wordIds = userWordWithWord.stream()
                .map(tuple -> tuple.get(1, Long.class))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, String> meaningByWordId = new java.util.HashMap<>();
        if (!wordIds.isEmpty()) {
            List<Tuple> meanings = queryFactory
                    .select(wordMeaning.word.id, wordMeaning.meaningKo)
                    .from(wordMeaning)
                    .where(wordMeaning.word.id.in(wordIds))
                    .orderBy(wordMeaning.id.asc())
                    .fetch();

            log.info("📚 [두번째 메서드] 조회된 word_meaning 개수: {}", meanings.size());

            // 각 word별 첫 번째 의미만 저장
            meanings.forEach(tuple -> {
                Long wId = tuple.get(0, Long.class);
                String meaning = tuple.get(1, String.class);
                log.info("💾 [두번째 메서드] Word ID {} -> 의미: {}", wId, meaning);
                meaningByWordId.putIfAbsent(wId, meaning);
            });
        }

        // Step 3: DTO 생성
        List<UserWordSearchDto> dtoList = userWordWithWord.stream()
                .map(tuple -> {
                    Long userWordId = tuple.get(0, Long.class);
                    Long wordId = tuple.get(1, Long.class);
                    String customMeaning = tuple.get(2, String.class);
                    String customText = tuple.get(3, String.class);
                    String wordText = tuple.get(4, String.class);
                    java.time.LocalDateTime createdAt = tuple.get(5, java.time.LocalDateTime.class);
                    WordType wordType = tuple.get(6, WordType.class);

                    String meaningKo = customMeaning != null ? customMeaning : meaningByWordId.get(wordId);
                    String text = wordText != null ? wordText : customText;

                    log.info("🏷️ [두번째 메서드] DTO 생성: text={}, meaningKo={}, wordId={}", text, meaningKo, wordId);

                    return new UserWordSearchDto(
                            wordId != null ? wordId : userWordId,
                            meaningKo,
                            text,
                            createdAt,
                            userWordId,
                            wordType
                    );
                })
                .toList();

        // DTO를 인터페이스 타입으로 변환
        List<WordSearchDtoProjection> projectionContent = dtoList.stream()
                .map(dto -> (WordSearchDtoProjection) dto)
                .toList();

        long total = userWordIdsWithTags.size(); // 전체 개수는 태그가 있는 UserWord 개수
        return new PageImpl<>(projectionContent, pageable, total);
    }


    /**
     * 기본 조건 생성 (JPAExpressions 제거 버전)
     * 날짜와 검색어 필터 조건 생성
     */
    private BooleanExpression buildBaseConditions(Long userId, String query, LocalDate fromDate, LocalDate toDate) {
        BooleanExpression conditions = userWord.user.id.eq(userId);

        // 날짜 필터
        if (fromDate != null) {
            conditions = conditions.and(userWord.createdAt.goe(fromDate.atStartOfDay()));
        }
        if (toDate != null) {
            conditions = conditions.and(userWord.createdAt.loe(toDate.plusDays(1).atStartOfDay()));
        }

        // 검색어 필터 (Word 또는 커스텀 단어 모두 검색)
        // word가 null일 수 있으므로 null 체크 필요
        if (query != null && !query.trim().isEmpty()) {
            String trimmedQuery = query.trim();
            BooleanExpression wordSearch = word.isNotNull().and(word.text.containsIgnoreCase(trimmedQuery));
            BooleanExpression customSearch = userWord.customText.containsIgnoreCase(trimmedQuery);
            conditions = conditions.and(wordSearch.or(customSearch));
        }

        return conditions;
    }

    private BooleanExpression buildDateAndQueryConditions(String query, LocalDate fromDate, LocalDate toDate) {
        BooleanExpression conditions = userWord.id.isNotNull(); // 항상 참인 기본 조건

        // 날짜 필터
        if (fromDate != null) {
            conditions = conditions.and(userWord.createdAt.goe(fromDate.atStartOfDay()));
        }
        if (toDate != null) {
            conditions = conditions.and(userWord.createdAt.loe(toDate.plusDays(1).atStartOfDay()));
        }

        // 검색어 필터 (Word 또는 커스텀 단어 모두 검색)
        // word가 null일 수 있으므로 null 체크 필요
        if (query != null && !query.trim().isEmpty()) {
            String trimmedQuery = query.trim();
            BooleanExpression wordSearch = word.isNotNull().and(word.text.containsIgnoreCase(trimmedQuery));
            BooleanExpression customSearch = userWord.customText.containsIgnoreCase(trimmedQuery);
            conditions = conditions.and(wordSearch.or(customSearch));
        }

        return conditions;
    }
}
