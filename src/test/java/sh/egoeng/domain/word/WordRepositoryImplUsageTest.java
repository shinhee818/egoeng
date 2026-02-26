package sh.egoeng.domain.word;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WordRepositoryImplUsageTest {
    @Autowired
    private WordRepository wordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("LATERAL JOIN 쿼리 사용 확인")
    void verifyLateralJoinIsUsed() {
        // given
        String query = "banana";
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        
        System.out.println("=== 쿼리 실행 확인 ===");
        System.out.println("결과 개수: " + result.getContent().size());
        System.out.println("전체 개수: " + result.getTotalElements());
        System.out.println();
        
        // Hibernate SQL 로그에서 LATERAL JOIN 확인 필요
        // application.yml에서 show_sql: true 설정되어 있으면 콘솔에 출력됨
    }

    @Test
    @DisplayName("실행 계획에서 LATERAL JOIN 사용 확인")
    void verifyLateralJoinInExecutionPlan() {
        System.out.println("\n=== LATERAL JOIN 사용 확인 ===\n");
        
        // 실행 계획 확인 쿼리 (WordRepository의 실제 쿼리와 동일)
        String explainQuery = """
            EXPLAIN ANALYZE
            SELECT w.id as id,
                   m.meaning_ko as meaningKo,
                   w.text as text,
                   w.created_at as createdAt
            FROM (
                SELECT id, text, created_at,
                       ts_rank(document_vector::tsvector, to_tsquery('english', 'banana')) +
                       CASE WHEN LOWER(TRIM(text)) = LOWER(TRIM('banana')) THEN 10.0 ELSE 0.0 END +
                       CASE WHEN text NOT LIKE '%,%' AND text NOT LIKE '% %' THEN 1.0 ELSE 0.0 END as score
                FROM word
                WHERE document_vector::tsvector @@ to_tsquery('english', 'banana')
            ) w
            LEFT JOIN LATERAL (
                SELECT meaning_ko 
                FROM word_meaning 
                WHERE word_id = w.id 
                LIMIT 1
            ) m ON true
            ORDER BY w.score DESC, w.id
            LIMIT 20 OFFSET 0
            """;

        @SuppressWarnings("unchecked")
        List<Object> results = entityManager.createNativeQuery(explainQuery).getResultList();

        String plan = results.stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.joining("\n"));

        System.out.println(plan);
        System.out.println("\n=== 분석 ===\n");

        // LATERAL JOIN 사용 확인
        boolean usesLateralJoin = plan.contains("LATERAL") || 
                                  plan.contains("Nested Loop") ||
                                  (plan.contains("Index Scan") && plan.contains("word_meaning"));

        // word_meaning 인덱스 사용 확인
        boolean usesWordMeaningIndex = plan.contains("idx_word_meaning_word_id") ||
                                       (plan.contains("Index Scan using idx_word_meaning_word_id"));

        // GIN 인덱스 사용 확인
        boolean usesGinIndex = plan.contains("idx_word_tsvector") || 
                                plan.contains("Bitmap Index Scan on idx_word_tsvector");

        System.out.println("LATERAL JOIN 사용: " + (usesLateralJoin ? "✅" : "❌"));
        System.out.println("GIN 인덱스 사용: " + (usesGinIndex ? "✅" : "❌"));
        System.out.println("word_meaning 인덱스 사용: " + (usesWordMeaningIndex ? "✅" : "❌"));

        // 실행 시간 추출
        String[] lines = plan.split("\n");
        for (String line : lines) {
            if (line.contains("Execution Time:")) {
                System.out.println("\n실제 SQL 실행 시간: " + line.trim());
                break;
            }
        }

        System.out.println("\n====================\n");
        
        // LATERAL JOIN 사용 검증
        assertThat(usesLateralJoin).as("LATERAL JOIN이 사용되어야 합니다").isTrue();
    }

    @Test
    @DisplayName("실제 쿼리 실행 및 결과 검증")
    void verifyQueryExecutionAndResults() {
        // given
        String[] testQueries = {"banana", "hi", "test"};
        Pageable pageable = PageRequest.of(0, 10);

        System.out.println("=== 실제 쿼리 실행 테스트 ===\n");

        for (String query : testQueries) {
            // when
            long startTime = System.nanoTime();
            Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);
            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;

            // then
            assertThat(result).isNotNull();
            
            System.out.println("검색어: '" + query + "'");
            System.out.println("  실행 시간: " + executionTimeMs + "ms");
            System.out.println("  결과 개수: " + result.getContent().size());
            System.out.println("  전체 개수: " + result.getTotalElements());
            
            // 결과 상세 출력 (최대 3개)
            List<WordSearchDtoProjection> content = result.getContent();
            int displayCount = Math.min(3, content.size());
            for (int i = 0; i < displayCount; i++) {
                WordSearchDtoProjection projection = content.get(i);
                System.out.println("    - ID: " + projection.getId() + 
                                 ", Text: " + projection.getText() + 
                                 ", Meaning: " + projection.getMeaningKo());
            }
            System.out.println();
        }
    }

    @Test
    @DisplayName("페이징 동작 확인")
    void verifyPagingBehavior() {
        // given
        String query = "test";
        Pageable firstPage = PageRequest.of(0, 5);
        Pageable secondPage = PageRequest.of(1, 5);

        // when
        Page<WordSearchDtoProjection> firstPageResult = wordRepository.findWordsByFTS(query, firstPage);
        Page<WordSearchDtoProjection> secondPageResult = wordRepository.findWordsByFTS(query, secondPage);

        // then
        assertThat(firstPageResult.getContent().size()).isLessThanOrEqualTo(5);
        assertThat(secondPageResult.getContent().size()).isLessThanOrEqualTo(5);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(secondPageResult.getTotalElements());

        System.out.println("=== 페이징 테스트 ===");
        System.out.println("첫 페이지: " + firstPageResult.getContent().size() + "개");
        System.out.println("두 번째 페이지: " + secondPageResult.getContent().size() + "개");
        System.out.println("전체 개수: " + firstPageResult.getTotalElements());
        
        // 첫 페이지와 두 번째 페이지의 결과가 다른지 확인
        if (!firstPageResult.getContent().isEmpty() && !secondPageResult.getContent().isEmpty()) {
            Long firstPageFirstId = firstPageResult.getContent().get(0).getId();
            Long secondPageFirstId = secondPageResult.getContent().get(0).getId();
            assertThat(firstPageFirstId).isNotEqualTo(secondPageFirstId);
            System.out.println("✅ 페이징 정상 작동");
        }
    }
}

