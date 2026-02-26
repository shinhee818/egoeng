package sh.egoeng.api.word.controller.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.word.controller.query.dto.LatestUserWordSearchResponse;
import sh.egoeng.api.word.controller.query.dto.UserWordSearchResponse;
import sh.egoeng.api.word.service.query.UserWordQueryService;
import sh.egoeng.api.word.service.query.dto.QueryUserWordSearchResult;
import sh.egoeng.api.word.service.query.dto.QueryUserWordResult;
import sh.egoeng.domain.word.LearningStatus;
import sh.egoeng.security.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-words")
public class UserWordQueryController {
    private final UserWordQueryService userWordQueryService;

    @GetMapping("/query")
    public UserWordSearchResponse searchMyWords(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) LearningStatus learningStatus
    ) {
        // 프론트에서 1 기반 페이지 번호를 받아서 0 기반으로 변환
        int pageNum = page != null ? page : 1;
        // 1 미만이면 1로 설정
        if (pageNum < 1) {
            pageNum = 1;
        }
        int pageIndex = pageNum - 1;

        var serviceResult = userWordQueryService.searchMyWordsByText(
                SecurityUtils.currentId(), 
                query, 
                fromDate,
                toDate,
                tagIds,
                learningStatus,
                PageRequest.of(pageIndex, size)
        );
        return toResponse(serviceResult);
    }

    private UserWordSearchResponse toResponse(QueryUserWordSearchResult serviceResult) {
        List<UserWordSearchResponse.WordSearchResult> words = serviceResult.words().stream()
                .map(result -> new UserWordSearchResponse.WordSearchResult(
                        result.id(),
                        result.userWordId(),
                        result.meaningKo(),
                        result.text(),
                        result.createdAt(),
                        result.tags().stream()
                                .map(tag -> new sh.egoeng.api.word.controller.query.dto.TagInfo(
                                        tag.id(),
                                        tag.name(),
                                        tag.color()
                                ))
                                .toList(),
                        result.learningStatus() != null ? result.learningStatus().name() : "NEW"
                ))
                .toList();
        
        return new UserWordSearchResponse(
                words,
                serviceResult.totalElements(),
                serviceResult.totalPages(),
                serviceResult.currentPage(),
                serviceResult.pageSize(),
                serviceResult.hasNext(),
                serviceResult.hasPrevious()
        );
    }

    @GetMapping("/latest")
    public List<LatestUserWordSearchResponse> searchLatestWords() {
        List<QueryUserWordResult> results = userWordQueryService.searchLatestWords();
        return results.stream()
                .map(result -> new LatestUserWordSearchResponse(
                        result.id(),
                        result.userWordId(),
                        result.meaningKo(),
                        result.text(),
                        result.createdAt(),
                        result.tags().stream()
                                .map(tag -> new sh.egoeng.api.word.controller.query.dto.TagInfo(
                                        tag.id(),
                                        tag.name(),
                                        tag.color()
                                ))
                                .toList(),
                        result.learningStatus() != null ? result.learningStatus().name() : "NEW"
                ))
                .toList();
    }
}
