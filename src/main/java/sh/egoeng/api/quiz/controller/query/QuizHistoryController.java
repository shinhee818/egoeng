package sh.egoeng.api.quiz.controller.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.quiz.controller.dto.response.QuizHistoryGroupResponse;
import sh.egoeng.api.quiz.service.query.QuizHistoryService;
import sh.egoeng.security.SecurityUtils;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz")
public class QuizHistoryController {
    private final QuizHistoryService quizHistoryService;

    @GetMapping("/history")
    public ResponseEntity<Page<QuizHistoryGroupResponse>> getQuizHistory(
            @RequestParam(required = false) String quizType,
            @RequestParam(defaultValue = "20", required = false) Integer size,
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<QuizHistoryGroupResponse> history = quizHistoryService.getQuizHistory(
                SecurityUtils.currentId(),
                quizType,
                fromDate,
                toDate,
                pageable
        );

        return ResponseEntity.ok(history);
    }
}





