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
class WordRepositoryGinIndexTest {
    @Autowired
    private WordRepository wordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("GIN 인덱스 사용 여부 확인 - EXPLAIN ANALYZE")
    void verifyGinIndexUsage() {
        System.out.println("\n=== GIN 인덱스 사용 확인 ===\n");
        
        // WordRepository의 실제 쿼리와 동일한 실행 계획 확인
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

        System.out.println("=== 실행 계획 ===");
        System.out.println(plan);
        System.out.println("\n=== 분석 결과 ===\n");

        // GIN 인덱스 사용 확인
        boolean usesGinIndex = plan.contains("idx_word_tsvector") || 
                                plan.contains("Bitmap Index Scan on idx_word_tsvector") ||
                                (plan.contains("Bitmap Index Scan") && plan.contains("word"));

        // word_meaning 인덱스 사용 확인
        boolean usesWordMeaningIndex = plan.contains("idx_word_meaning_word_id") ||
                                       plan.contains("Index Scan using idx_word_meaning_word_id");

        // Seq Scan 사용 여부 (인덱스 미사용)
        boolean usesSeqScanWord = plan.contains("Seq Scan on word");
        boolean usesSeqScanWordMeaning = plan.contains("Seq Scan on word_meaning");
        boolean usesParallelScan = plan.contains("Parallel Seq Scan");

        // JOIN 방식 확인
        boolean usesNestedLoop = plan.contains("Nested Loop");
        boolean usesHashJoin = plan.contains("Hash") && plan.contains("Join");

        System.out.println("✅ GIN 인덱스 사용: " + (usesGinIndex ? "YES ✅" : "NO ❌"));
        System.out.println("✅ word_meaning 인덱스 사용: " + (usesWordMeaningIndex ? "YES ✅" : "NO ❌"));
        System.out.println("❌ word Seq Scan: " + (usesSeqScanWord ? "YES (인덱스 미사용)" : "NO ✅"));
        System.out.println("❌ word_meaning Seq Scan: " + (usesSeqScanWordMeaning ? "YES (인덱스 미사용)" : "NO ✅"));
        System.out.println("⚠️ 병렬 스캔: " + (usesParallelScan ? "YES (성능 저하 가능)" : "NO ✅"));
        System.out.println("JOIN 방식: " + (usesNestedLoop ? "✅ Nested Loop" : usesHashJoin ? "⚠️ Hash Join" : "?"));

        // 실행 시간 추출
        String[] lines = plan.split("\n");
        for (String line : lines) {
            if (line.contains("Execution Time:")) {
                System.out.println("\n실제 SQL 실행 시간: " + line.trim());
                break;
            }
        }

        System.out.println("\n=== 인덱스 사용 확인 방법 ===");
        System.out.println("1. 'Bitmap Index Scan on idx_word_tsvector' 또는 'Index Scan using idx_word_tsvector'가 보이면 GIN 인덱스 사용 ✅");
        System.out.println("2. 'Seq Scan on word'가 보이면 인덱스 미사용 ❌");
        System.out.println("3. 'Parallel Seq Scan'이 보이면 병렬 스캔 사용 (인덱스 미사용 가능) ⚠️");
        System.out.println("====================\n");
        
        // 검증
        assertThat(usesGinIndex).as("GIN 인덱스(idx_word_tsvector)가 사용되어야 합니다").isTrue();
        assertThat(usesSeqScanWord).as("word 테이블에서 Seq Scan이 사용되면 안 됩니다").isFalse();
    }

    @Test
    @DisplayName("실제 쿼리 실행 및 성능 확인")
    void verifyQueryPerformance() {
        // given
        String query = "banana";
        Pageable pageable = PageRequest.of(0, 20);

        // Warm-up
        wordRepository.findWordsByFTS(query, pageable);

        // 성능 측정
        long totalTime = 0;
        int iterations = 10;

        System.out.println("=== 성능 측정 ===");
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);
            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;
            totalTime += executionTimeMs;
            
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isNotEmpty();
        }

        long averageTime = totalTime / iterations;
        System.out.println("평균 실행 시간 (" + iterations + "회): " + averageTime + "ms");
        System.out.println("인덱스 사용 시 예상 시간: ~15-25ms");
        System.out.println("인덱스 미사용 시 예상 시간: ~100ms 이상");
        
        // 인덱스 사용 시 성능이 좋아야 함
        assertThat(averageTime).as("인덱스 사용 시 100ms 이하여야 합니다").isLessThan(100);
    }
}

















