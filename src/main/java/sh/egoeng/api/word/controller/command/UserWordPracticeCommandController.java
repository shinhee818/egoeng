package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.word.controller.dto.response.UserWordPracticeCompleteResponse;
import sh.egoeng.api.word.controller.dto.response.UserWordPracticeResponse;
import sh.egoeng.api.word.service.command.UserWordPracticeService;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.domain.word.service.UserWordService;
import sh.egoeng.security.SecurityUtils;

/**
 * 유저 단어 학습 컨트롤러
 * 퀴즈와 별개로 단순히 단어를 학습하는 기능
 */
@RestController
@RequestMapping("/api/user-words/practice")
@RequiredArgsConstructor
public class UserWordPracticeCommandController {
    private final UserWordPracticeService userWordPracticeService;
    private final UserWordService userWordService;

    /**
     * 유저 단어장에서 랜덤으로 단어를 선택하여 학습 시작
     *
     * @param limit 가져올 단어 개수 (기본값: 20)
     * @return 학습할 단어 목록
     */
    @GetMapping("/start")
    public UserWordPracticeResponse startPractice(
            @RequestParam(required = false, defaultValue = "20") Integer limit
    ) {
        return userWordPracticeService.startPractice(limit);
    }

    /**
     * 단어 학습 완료 (practiceCount, lastPracticedAt 업데이트)
     *
     * @param userWordId 유저 단어 ID
     * @return 업데이트된 정보
     */
    @PostMapping("/{userWordId}/complete")
    public UserWordPracticeCompleteResponse completePractice(@PathVariable Long userWordId) {
        Long userId = SecurityUtils.currentId();
        userWordPracticeService.completePractice(userId, userWordId);

        UserWord userWord = userWordService.findById(userWordId)
                .orElseThrow(() -> new IllegalArgumentException("UserWord not found: " + userWordId));

        return new UserWordPracticeCompleteResponse(
                userWord.getId(),
                userWord.getPracticeCount(),
                userWord.getLastPracticedAt()
        );
    }
}

