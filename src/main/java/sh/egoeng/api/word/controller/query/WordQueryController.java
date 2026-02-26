package sh.egoeng.api.word.controller.query;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.word.controller.query.dto.WordSearchResponse;
import sh.egoeng.api.word.service.query.WordQueryService;
import sh.egoeng.api.word.service.query.dto.QueryWordSearchResultWithPaging;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/words")
public class WordQueryController {
    private final WordQueryService wordQueryService;

    @GetMapping("/search")
    public WordSearchResponse searchWordsByText(
            @RequestParam String query,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = "0", required = false) Integer page
    ) {
        var serviceResult = wordQueryService.searchWords(query, page, size);
        return toResponse(serviceResult);
    }

    private WordSearchResponse toResponse(QueryWordSearchResultWithPaging serviceResult) {
        List<WordSearchResponse.WordSearchResult> words = serviceResult.words().stream()
                .map(result -> {
                    // 첫 번째 의미만 사용 (API 스펙에 맞게)
                    var firstMeaning = result.meanings().stream().findFirst().orElse(null);

                    return new WordSearchResponse.WordSearchResult(
                            result.id(),
                            result.text(),
                            firstMeaning != null ? firstMeaning.meaningKo() : "",
                            firstMeaning != null ? firstMeaning.partOfSpeech() : "",
                            "", // difficulty - 추후 추가 필요
                            firstMeaning != null ? firstMeaning.example() : "",
                            "", // exampleSentenceKo - 추후 추가 필요
                            "", // pronunciation - 추후 추가 필요
                            0, // popularityScore - 추후 추가 필요
                            result.createdAt(),
                            false // isUserAdded - 추후 추가 필요
                    );
                })
                .toList();

        return new WordSearchResponse(
                words,
                serviceResult.totalElements(),
                serviceResult.totalPages(),
                serviceResult.currentPage(),
                serviceResult.pageSize(),
                serviceResult.hasNext(),
                serviceResult.hasPrevious()
        );
    }
}