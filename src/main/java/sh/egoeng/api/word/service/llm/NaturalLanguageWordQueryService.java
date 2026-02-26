package sh.egoeng.api.word.service.llm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.word.UserWordSearchRepository;
import sh.egoeng.domain.word.service.WordSearchDtoProjection;
import sh.egoeng.feign.llm.chat.LlmChatClient;
import sh.egoeng.feign.llm.chat.dto.request.ChatRequest;
import sh.egoeng.feign.llm.chat.dto.response.ChatResult;
import sh.egoeng.feign.llm.quiz.dto.response.LlmResponse;
import sh.egoeng.security.SecurityUtils;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaturalLanguageWordQueryService {
    
    private final LlmChatClient llmChatClient;
    private final UserWordSearchRepository userWordSearchRepository;
    
    /**
     * 자연어로 단어 검색
     * 예: "최근에 등록한 단어 보여줘", "apple이 포함된 단어 찾아줘", "일주일 전에 등록한 단어"
     */
    @Transactional(readOnly = true)
    public NaturalLanguageQueryResponse searchByNaturalLanguage(String naturalQuery) {
        Long userId = SecurityUtils.currentId();
        
        // LLM에게 자연어를 구조화된 쿼리로 변환 요청
        QueryInterpretation interpretation = interpretQuery(naturalQuery);
        
        log.info("Interpreted query: {}", interpretation);
        
        // 해석된 쿼리로 실제 검색 수행
        Page<WordSearchDtoProjection> results = userWordSearchRepository.findUserWordsWithoutTags(
                userId,
                interpretation.searchText(),
                interpretation.fromDate(),
                interpretation.toDate(),
                null,  // learningStatus: 학습 상태별로 필터링하지 않음
                PageRequest.of(0, interpretation.limit())
        );
        
        return new NaturalLanguageQueryResponse(
                naturalQuery,
                interpretation,
                results.getContent(),
                results.getTotalElements()
        );
    }
    
    /**
     * LLM을 사용하여 자연어를 구조화된 쿼리로 변환
     */
    private QueryInterpretation interpretQuery(String naturalQuery) {
        String systemPrompt = """
                You are a query interpreter for a word learning application.
                Convert natural language queries into structured query parameters.
                
                User can ask things like:
                - "최근에 등록한 단어 보여줘" → recent words
                - "apple이 포함된 단어" → search for "apple"
                - "일주일 전에 등록한 단어" → words from last week
                - "오늘 등록한 단어" → today's words
                
                Response format (JSON):
                {
                    "searchText": "검색할 텍스트 (없으면 null)",
                    "daysAgo": 7 (며칠 전부터인지, null이면 전체 기간),
                    "limit": 10 (결과 개수, 기본 10)
                }
                
                Only respond with valid JSON, no explanation.
                """;
        
        try {
            Long userId = SecurityUtils.currentId();
            ChatRequest request = ChatRequest.builder()
                    .message(systemPrompt + "\n\nQuery: " + naturalQuery)
                    .userId(userId)
                    .build();
            
            LlmResponse<ChatResult> response = llmChatClient.chat(request);
            String llmResponse = response.getResult().getMessage();
            log.info("LLM response: {}", llmResponse);
            
            // JSON 파싱
            return parseInterpretation(llmResponse, naturalQuery);
        } catch (Exception e) {
            log.error("Failed to interpret query with LLM", e);
            // LLM 실패 시 기본값 반환
            return new QueryInterpretation(null, null, null, 10);
        }
    }
    
    /**
     * LLM 응답을 파싱하여 QueryInterpretation 객체로 변환
     */
    private QueryInterpretation parseInterpretation(String llmResponse, String originalQuery) {
        try {
            // JSON에서 중괄호 추출
            String json = llmResponse;
            if (!json.trim().startsWith("{")) {
                int start = json.indexOf("{");
                int end = json.lastIndexOf("}");
                if (start >= 0 && end > start) {
                    json = json.substring(start, end + 1);
                }
            }
            
            // 간단한 JSON 파싱 (실제로는 ObjectMapper 사용 권장)
            String searchText = extractJsonValue(json, "searchText");
            Integer daysAgo = extractJsonIntValue(json, "daysAgo");
            Integer limit = extractJsonIntValue(json, "limit");
            
            if (limit == null || limit <= 0) {
                limit = 10;
            }
            
            LocalDate fromDate = null;
            if (daysAgo != null && daysAgo > 0) {
                fromDate = LocalDate.now().minusDays(daysAgo);
            }
            
            return new QueryInterpretation(
                    searchText,
                    fromDate,
                    LocalDate.now(),
                    limit
            );
        } catch (Exception e) {
            log.error("Failed to parse LLM response", e);
            return new QueryInterpretation(null, null, null, 10);
        }
    }
    
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    private Integer extractJsonIntValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return null;
    }
    
    /**
     * LLM이 해석한 쿼리 구조
     */
    public record QueryInterpretation(
            String searchText,
            LocalDate fromDate,
            LocalDate toDate,
            int limit
    ) {}
    
    /**
     * 자연어 쿼리 응답
     */
    public record NaturalLanguageQueryResponse(
            String originalQuery,
            QueryInterpretation interpretation,
            List<WordSearchDtoProjection> results,
            long totalCount
    ) {}
}
