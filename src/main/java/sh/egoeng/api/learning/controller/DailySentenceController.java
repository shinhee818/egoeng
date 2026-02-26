package sh.egoeng.api.learning.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.learning.controller.dto.response.DailySentenceCompleteResponse;
import sh.egoeng.api.learning.controller.dto.response.DailySentenceResponse;
import sh.egoeng.api.learning.service.DailySentenceService;
import sh.egoeng.security.SecurityUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning/daily-sentence")
public class DailySentenceController {
    private final DailySentenceService dailySentenceService;

    @GetMapping("/today")
    public DailySentenceResponse getTodaySentence() {
        return dailySentenceService.getRandomSentence();
    }

    @PostMapping("/{dailySentenceId}/complete")
    public DailySentenceCompleteResponse completeLearning(@PathVariable Long dailySentenceId) {
        Long userId = SecurityUtils.currentId();
        return dailySentenceService.completeLearning(userId, dailySentenceId);
    }
}













