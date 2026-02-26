package sh.egoeng.api.word.controller.query;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.learning.controller.dto.response.LearningWordQueryResponse;
import sh.egoeng.api.word.service.query.UserWordStudyQueryService;
import sh.egoeng.domain.word.LearningStatus;
import sh.egoeng.security.SecurityUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-words")
public class UserWordStudyQueryController {

    private final UserWordStudyQueryService userWordStudyQueryService;

    @GetMapping("/study")
    public LearningWordQueryResponse getStudyWords(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) List<String> status
    ) {
        Long userId = SecurityUtils.currentId();
        int pageSize = limit != null ? limit : 20;
        // status가 null이거나 비어있으면 null로 전달하여 모든 상태 조회
        List<LearningStatus> statuses = parseStatuses(status);
        
        return userWordStudyQueryService.getLearningWords(userId, cursor, pageSize, statuses);
    }

    private List<LearningStatus> parseStatuses(List<String> statusStrings) {
        // status가 null이거나 비어있으면 NEW, LEARNING, MASTERED만 조회 (REVIEWING 제외)
        if (statusStrings == null || statusStrings.isEmpty()) {
            return List.of(LearningStatus.NEW, LearningStatus.LEARNING, LearningStatus.MASTERED);
        }
        return statusStrings.stream()
                .map(LearningStatus::valueOf)
                .toList();
    }
}

