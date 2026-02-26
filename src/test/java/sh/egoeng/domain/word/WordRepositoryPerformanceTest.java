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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WordRepositoryPerformanceTest {
    @Autowired
    private WordRepository wordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("FTS 성능 테스트 - 여러 번 실행하여 평균 측정")
    void findWordsByFTS_performance() {
        // given
        String query = "banana";
        Pageable pageable = PageRequest.of(0, 20);

        // 첫 실행 (warm-up)
        System.out.println("=== Warm-up 실행 ===");
        wordRepository.findWordsByFTS(query, pageable);
        System.out.println("Warm-up 완료\n");

        // 성능 측정 (10회 실행 평균)
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        int iterations = 10;

        System.out.println("=== 성능 측정 시작 (" + iterations + "회) ===");
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);
            long endTime = System.nanoTime();
            long executionTimeNanos = endTime - startTime;
            long executionTimeMs = executionTimeNanos / 1_000_000;
            
            totalTime += executionTimeMs;
            minTime = Math.min(minTime, executionTimeMs);
            maxTime = Math.max(maxTime, executionTimeMs);
            
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isNotEmpty();
            
            System.out.println((i + 1) + "회차: " + executionTimeMs + "ms");
        }

        long averageTime = totalTime / iterations;
        System.out.println("\n=== 성능 결과 ===");
        System.out.println("평균 실행 시간: " + averageTime + "ms");
        System.out.println("최소 실행 시간: " + minTime + "ms");
        System.out.println("최대 실행 시간: " + maxTime + "ms");
        System.out.println("총 실행 시간: " + totalTime + "ms");
        System.out.println("================\n");
        
        // 성능 검증 (인덱스 사용 시 500ms 이하)
        assertThat(averageTime).isLessThan(500);
    }

    @Test
    @DisplayName("다양한 검색어 성능 비교")
    void findWordsByFTS_differentQueries() {
        String[] queries = {"banana", "hi", "test", "hello", "word"};
        Pageable pageable = PageRequest.of(0, 20);

        System.out.println("=== 다양한 검색어 성능 비교 ===\n");
        
        for (String query : queries) {
            long startTime = System.nanoTime();
            Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);
            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;
            
            System.out.println("검색어: '" + query + "'");
            System.out.println("  실행 시간: " + executionTimeMs + "ms");
            System.out.println("  결과 개수: " + result.getContent().size());
            System.out.println("  전체 개수: " + result.getTotalElements());
            System.out.println();
        }
    }

    @Test
    @DisplayName("페이징 성능 테스트")
    void findWordsByFTS_pagingPerformance() {
        String query = "test";
        int pageSize = 20;
        int maxPages = 5;

        System.out.println("=== 페이징 성능 테스트 ===\n");
        
        for (int page = 0; page < maxPages; page++) {
            Pageable pageable = PageRequest.of(page, pageSize);
            
            long startTime = System.nanoTime();
            Page<WordSearchDtoProjection> result = wordRepository.findWordsByFTS(query, pageable);
            long endTime = System.nanoTime();
            long executionTimeMs = (endTime - startTime) / 1_000_000;
            
            System.out.println("페이지 " + (page + 1) + ": " + executionTimeMs + "ms (" + 
                             result.getContent().size() + "개 결과)");
        }
        System.out.println();
    }

    @Test
    @DisplayName("FTS 실행 계획 확인 및 인덱스 사용 여부 검증")
    void checkExecutionPlan() {
        System.out.println("\n=== 실행 계획 확인 ===\n");
        
        // 실행 계획 확인 쿼리
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
            LEFT JOIN word_meaning m ON w.id = m.word_id
            ORDER BY w.score DESC, w.id, m.id
            LIMIT 20 OFFSET 0
            """;

        @SuppressWarnings("unchecked")
        List<Object> results = entityManager.createNativeQuery(explainQuery).getResultList();

        String plan = results.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        System.out.println(plan);
        System.out.println("\n=== 인덱스 사용 분석 ===\n");

        // GIN 인덱스 사용 확인
        boolean usesGinIndex = plan.contains("idx_word_tsvector") || 
                                plan.contains("Bitmap Index Scan on idx_word_tsvector") ||
                                (plan.contains("Bitmap Index Scan") && plan.contains("word"));
        
        // word_meaning 인덱스 사용 확인 (정확한 확인)
        boolean usesWordMeaningIndex = plan.contains("idx_word_meaning_word_id") ||
                                       (plan.contains("Index Scan using idx_word_meaning_word_id"));

        // 병렬 스캔 사용 여부 확인
        boolean usesParallelScan = plan.contains("Parallel Seq Scan") || 
                                   (plan.contains("Gather") && !plan.contains("Gather Merge"));

        // Seq Scan 사용 여부 확인
        boolean usesSeqScanWord = plan.contains("Seq Scan on word");
        boolean usesSeqScanWordMeaning = plan.contains("Seq Scan on word_meaning");
        
        // JOIN 방식 확인
        boolean usesNestedLoop = plan.contains("Nested Loop");
        boolean usesHashJoin = plan.contains("Hash") && plan.contains("Join");

        System.out.println("GIN 인덱스 사용: " + (usesGinIndex ? "✅" : "❌"));
        System.out.println("word_meaning 인덱스 사용: " + (usesWordMeaningIndex ? "✅" : "❌"));
        System.out.println("word Seq Scan 사용: " + (usesSeqScanWord ? "❌ (인덱스 미사용)" : "✅"));
        System.out.println("word_meaning Seq Scan 사용: " + (usesSeqScanWordMeaning ? "❌ (인덱스 미사용)" : "✅"));
        System.out.println("병렬 스캔 사용: " + (usesParallelScan ? "⚠️ (성능 저하 가능)" : "✅"));
        System.out.println("JOIN 방식: " + (usesNestedLoop ? "✅ Nested Loop" : usesHashJoin ? "⚠️ Hash Join" : "?"));

        // 실행 시간 추출
        String[] lines = plan.split("\n");
        for (String line : lines) {
            if (line.contains("Execution Time:")) {
                System.out.println("\n실제 SQL 실행 시간: " + line.trim());
                break;
            }
        }

        System.out.println("\n====================\n");
        
        // 인덱스 사용 검증
        assertThat(usesGinIndex).as("GIN 인덱스가 사용되어야 합니다").isTrue();
    }
}

