package sh.egoeng.api.word.controller.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.word.controller.query.dto.UserWordSearchResponse;
import sh.egoeng.api.word.service.query.UserWordReviewService;
import sh.egoeng.api.word.service.query.dto.QueryUserWordSearchResult;
import sh.egoeng.api.word.service.query.dto.QueryUserWordResult;
import sh.egoeng.security.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/user-words/review")
@RequiredArgsConstructor
public class UserWordReviewController {
    private final UserWordReviewService userWordReviewService;

    @GetMapping("/today")
    public UserWordSearchResponse getTodayReviewWords(
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(required = false) List<Long> tagIds
    ) {
        Long userId = SecurityUtils.currentId();
        var serviceResult = userWordReviewService.getTodayReviewWords(
                userId,
                tagIds,
                PageRequest.of(page - 1, size)
        );
        return toResponse(serviceResult);
    }

    @GetMapping("/overdue")
    public UserWordSearchResponse getOverdueReviewWords(
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(required = false) List<Long> tagIds
    ) {
        Long userId = SecurityUtils.currentId();
        var serviceResult = userWordReviewService.getOverdueReviewWords(
                userId,
                tagIds,
                PageRequest.of(page - 1, size)
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
}




