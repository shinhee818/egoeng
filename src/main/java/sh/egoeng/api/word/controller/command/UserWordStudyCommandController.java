package sh.egoeng.api.word.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.word.controller.dto.request.BulkStudyStartRequest;
import sh.egoeng.api.word.controller.dto.request.StudyReviewRequest;
import sh.egoeng.api.word.controller.dto.request.StudyStartRequest;
import sh.egoeng.api.word.controller.dto.response.BulkStudyStartResponse;
import sh.egoeng.api.word.controller.dto.response.StudyCompleteResponse;
import sh.egoeng.api.word.controller.dto.response.StudyMasterResponse;
import sh.egoeng.api.word.controller.dto.response.StudyReviewResponse;
import sh.egoeng.api.word.controller.dto.response.StudyStartResponse;
import sh.egoeng.api.word.service.command.UserWordStudyService;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.service.UserWordService;
import sh.egoeng.security.SecurityUtils;

import java.util.List;

@RestController
@RequestMapping("/api/user-words")
@RequiredArgsConstructor
public class UserWordStudyCommandController {
    private final UserWordStudyService userWordStudyService;
    private final UserWordService userWordService;

    /**
     * 벌크 학습 시작
     * 여러 단어를 한 번에 학습 시작 (내 단어장 화면에서 사용)
     */
    @PostMapping("/study/bulk-start")
    public BulkStudyStartResponse bulkStartStudy(@RequestBody BulkStudyStartRequest request) {
        Long userId = SecurityUtils.currentId();
        List<Long> startedWordIds = userWordStudyService.bulkStartStudy(
                userId, 
                request.userWordIds(),
                request.learningGoal()
        );

        return new BulkStudyStartResponse(
                startedWordIds.size(),
                startedWordIds
        );
    }

    /**
     * 개별 단어 학습 시작
     */
    @PostMapping("/{userWordId}/study/start")
    public StudyStartResponse startStudy(
            @PathVariable Long userWordId,
            @RequestBody(required = false) StudyStartRequest request) {
        Long userId = SecurityUtils.currentId();
        Integer learningGoal = request != null ? request.learningGoal() : null;
        userWordStudyService.startStudy(userId, userWordId, learningGoal);

        UserWord userWord = userWordService.findById(userWordId).orElseThrow();

        return new StudyStartResponse(
                userWord.getId(),
                userWord.getLearningStatus(),
                userWord.getLastStudiedAt()
        );
    }

    /**
     * 개별 단어 학습 완료
     */
    @PostMapping("/{userWordId}/study/complete")
    public StudyCompleteResponse completeLearning(@PathVariable Long userWordId) {
        Long userId = SecurityUtils.currentId();
        userWordStudyService.completeLearning(userId, userWordId);

        UserWord userWord = userWordService.findById(userWordId).orElseThrow();

        return new StudyCompleteResponse(
                userWord.getId(),
                userWord.getLearningStatus(),
                userWord.getLastStudiedAt()
        );
    }

    /**
     * 개별 단어 복습 완료
     */
    @PostMapping("/{userWordId}/study/review")
    public StudyReviewResponse completeReview(
            @PathVariable Long userWordId,
            @RequestBody StudyReviewRequest request) {
        Long userId = SecurityUtils.currentId();
        userWordStudyService.completeReview(
                userId,
                userWordId,
                request.isCorrect(),
                request.difficulty()
        );

        UserWord userWord = userWordService.findById(userWordId).orElseThrow();

        return new StudyReviewResponse(
                userWord.getId(),
                request.isCorrect(),
                userWord.getLearningStatus(),
                userWord.getReviewCount(),
                userWord.getNextReviewDate(),
                userWord.getMasteryLevel(),
                userWord.getLastStudiedAt()
        );
    }

    /**
     * 개별 단어 암기 완료
     */
    @PostMapping("/{userWordId}/study/master")
    public StudyMasterResponse masterWord(@PathVariable Long userWordId) {
        Long userId = SecurityUtils.currentId();
        userWordStudyService.masterWord(userId, userWordId);

        UserWord userWord = userWordService.findById(userWordId).orElseThrow();

        return new StudyMasterResponse(
                userWord.getId(),
                userWord.getLearningStatus(),
                userWord.getMasteryLevel(),
                userWord.getLastStudiedAt()
        );
    }
}
