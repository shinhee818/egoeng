package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.word.exception.UserWordNotFoundException;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserWordStudyService {
    private final UserWordRepository userWordRepository;
    private final UserService userService;

    /**
     * 학습 시작
     * 
     * @param userId 유저 ID
     * @param userWordId 유저 단어 ID
     * @param learningGoal 학습 목표 개수 (선택적)
     */
    public void startStudy(Long userId, Long userWordId, Integer learningGoal) {
        userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new UserWordNotFoundException(userWordId));

        if (!userWord.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to study this word.");
        }

        userWord.startStudy(learningGoal);
        userWordRepository.save(userWord);
        userWordRepository.flush(); // 명시적 flush로 변경사항을 즉시 데이터베이스에 반영
    }

    /**
     * 학습 시작 (목표 없이)
     * 기존 호환성을 위한 오버로드 메서드
     */
    public void startStudy(Long userId, Long userWordId) {
        startStudy(userId, userWordId, null);
    }

    /**
     * 복습 완료
     */
    public void completeReview(Long userId, Long userWordId, Boolean isCorrect, Integer difficulty) {
        userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new UserWordNotFoundException(userWordId));

        if (!userWord.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to review this word.");
        }

        // 복습 횟수 증가
        int newReviewCount = (userWord.getReviewCount() != null ? userWord.getReviewCount() : 0) + 1;

        // 암기 정도 업데이트 (정답률 기반)
        int newMasteryLevel = calculateMasteryLevel(userWord.getMasteryLevel(), isCorrect, difficulty);

        // 다음 복습일 계산
        LocalDate nextReviewDate = calculateNextReviewDate(
                LocalDate.now(),
                newReviewCount,
                isCorrect ? 1.0 : 0.0
        );

        // 암기 완료 체크 (mastery_level이 90 이상이면 nextReviewDate를 null로 설정)
        if (newMasteryLevel >= 90) {
            nextReviewDate = null;
        }

        userWord.completeReview(isCorrect, newReviewCount, newMasteryLevel, nextReviewDate);
        userWordRepository.save(userWord);
        userWordRepository.flush(); // 명시적 flush로 변경사항을 즉시 데이터베이스에 반영
    }

    /**
     * 학습 완료
     * 학습 시작 후 단어를 보고 "알겠어요"를 누를 때 호출
     */
    public void completeLearning(Long userId, Long userWordId) {
        userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new UserWordNotFoundException(userWordId));

        if (!userWord.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to complete learning for this word.");
        }

        userWord.completeLearning();
        userWordRepository.save(userWord);
        userWordRepository.flush(); // 명시적 flush로 변경사항을 즉시 데이터베이스에 반영
    }

    /**
     * 암기 완료
     */
    public void masterWord(Long userId, Long userWordId) {
        userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new UserWordNotFoundException(userWordId));

        if (!userWord.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to master this word.");
        }

        userWord.master();
        userWordRepository.save(userWord);
        userWordRepository.flush(); // 명시적 flush로 변경사항을 즉시 데이터베이스에 반영
    }

    /**
     * 벌크 학습 시작
     * 여러 단어를 한 번에 학습 시작
     * 
     * @param userId 유저 ID
     * @param userWordIds 학습 시작할 단어 ID 목록
     * @param learningGoal 학습 목표 개수 (선택적)
     * @return 학습 시작된 단어 ID 목록
     */
    public List<Long> bulkStartStudy(Long userId, List<Long> userWordIds, Integer learningGoal) {
        userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (userWordIds == null || userWordIds.isEmpty()) {
            throw new IllegalArgumentException("학습 시작할 단어 ID 목록이 비어있습니다.");
        }

        // 모든 단어 조회
        List<UserWord> userWords = userWordRepository.findAllById(userWordIds);

        if (userWords.size() != userWordIds.size()) {
            throw new IllegalArgumentException("일부 단어를 찾을 수 없습니다.");
        }

        // 권한 확인 및 학습 시작
        List<Long> startedWordIds = new java.util.ArrayList<>();
        for (UserWord userWord : userWords) {
            if (!userWord.getUser().getId().equals(userId)) {
                throw new SecurityException("User does not have permission to study word: " + userWord.getId());
            }

            userWord.startStudy(learningGoal);
            startedWordIds.add(userWord.getId());
        }

        // 벌크 저장
        userWordRepository.saveAll(userWords);
        userWordRepository.flush(); // 명시적 flush로 변경사항을 즉시 데이터베이스에 반영

        return startedWordIds;
    }

    /**
     * 암기 정도 계산
     */
    private int calculateMasteryLevel(Integer currentLevel, Boolean isCorrect, Integer difficulty) {
        int current = currentLevel != null ? currentLevel : 0;
        
        if (isCorrect) {
            // 정답일 때: 난이도에 따라 증가량 조정
            int increment = difficulty != null ? difficulty * 5 : 10; // 1-5점이면 5-25점 증가
            return Math.min(100, current + increment);
        } else {
            // 오답일 때: 감소
            return Math.max(0, current - 10);
        }
    }

    /**
     * 다음 복습일 계산 (스페이싱 리피티션 알고리즘)
     */
    private LocalDate calculateNextReviewDate(LocalDate currentDate, int reviewCount, double correctRate) {
        // 기본 복습 주기
        int baseInterval;
        if (reviewCount == 1) {
            baseInterval = 1; // 1일
        } else if (reviewCount == 2) {
            baseInterval = 3; // 3일
        } else if (reviewCount == 3) {
            baseInterval = 7; // 7일
        } else if (reviewCount == 4) {
            baseInterval = 14; // 14일
        } else {
            baseInterval = 30; // 30일
        }

        // 정답률에 따른 조정
        double multiplier;
        if (correctRate >= 1.0) {
            multiplier = 1.5; // 완벽하게 맞춤
        } else if (correctRate >= 0.8) {
            multiplier = 1.0; // 잘 맞춤
        } else if (correctRate >= 0.5) {
            multiplier = 0.7; // 보통
        } else {
            multiplier = 0.1; // 못 맞춤 (다음날 다시)
        }

        int days = (int) (baseInterval * multiplier);
        return currentDate.plusDays(Math.max(1, days));
    }
}

