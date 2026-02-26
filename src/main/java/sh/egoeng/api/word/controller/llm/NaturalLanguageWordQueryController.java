package sh.egoeng.api.word.controller.llm;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.word.service.llm.NaturalLanguageWordQueryService;

@RestController
@RequestMapping("/api/words/natural-query")
@RequiredArgsConstructor
public class NaturalLanguageWordQueryController {
    
    private final NaturalLanguageWordQueryService naturalLanguageWordQueryService;
    
    /**
     * 자연어로 단어 검색
     * 
     * 예시:
     * - "최근에 등록한 단어 보여줘"
     * - "apple이 포함된 단어 찾아줘"
     * - "일주일 전에 등록한 단어"
     * - "오늘 등록한 단어 5개만"
     */
    @PostMapping
    public NaturalLanguageWordQueryService.NaturalLanguageQueryResponse search(
            @RequestBody NaturalQueryRequest request
    ) {
        return naturalLanguageWordQueryService.searchByNaturalLanguage(request.query());
    }
    
    record NaturalQueryRequest(String query) {}
}
