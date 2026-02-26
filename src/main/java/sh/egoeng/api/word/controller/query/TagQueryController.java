package sh.egoeng.api.word.controller.query;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.word.controller.query.dto.TagListResponse;
import sh.egoeng.api.word.service.query.TagQueryService;
import sh.egoeng.security.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagQueryController {
    private final TagQueryService tagQueryService;

    /**
     * 사용자가 등록한 모든 태그 목록 조회
     */
    @GetMapping
    public List<TagListResponse> getAllTags() {
        Long userId = SecurityUtils.currentId();
        return tagQueryService.getAllTagsByUserId(userId);
    }
}
















