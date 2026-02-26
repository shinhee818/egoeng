package sh.egoeng.api.learning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.learning.controller.dto.response.DailySentenceCompleteResponse;
import sh.egoeng.api.learning.controller.dto.response.DailySentenceResponse;
import sh.egoeng.domain.learning.DailySentence;
import sh.egoeng.domain.learning.DailySentenceRepository;
import sh.egoeng.domain.learning.UserSentence;
import sh.egoeng.domain.learning.UserSentenceRepository;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailySentenceService {

    private final DailySentenceRepository dailySentenceRepository;
    private final UserSentenceRepository userSentenceRepository;
    private final UserService userService;

    /**
     * 랜덤으로 오늘의 문장 조회
     * @return 랜덤하게 선택된 DailySentence (없으면 null)
     */
    public DailySentenceResponse getRandomSentence() {
        return dailySentenceRepository.findRandom()
                .map(this::toResponse)
                .orElse(null);
    }

    /**
     * 오늘의 문장 학습 완료
     * - 이미 UserSentence가 있으면 학습 완료 처리
     * - 없으면 UserSentence를 생성하고 학습 완료 처리
     * @param userId 사용자 ID
     * @param dailySentenceId DailySentence ID
     * @return 학습 완료 응답
     */
    @Transactional
    public DailySentenceCompleteResponse completeLearning(Long userId, Long dailySentenceId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        DailySentence dailySentence = dailySentenceRepository.findById(dailySentenceId)
                .orElseThrow(() -> new IllegalArgumentException("DailySentence not found: " + dailySentenceId));

        // 이미 UserSentence가 있는지 확인
        UserSentence userSentence = userSentenceRepository.findByUserAndDailySentence(user, dailySentence)
                .orElse(null);

        if (userSentence == null) {
            // 없으면 새로 생성
            userSentence = UserSentence.builder()
                    .user(user)
                    .dailySentence(dailySentence)
                    .sentence(dailySentence.getSentence())
                    .meaningKo(dailySentence.getMeaningKo())
                    .explanation(dailySentence.getExplanation())
                    .category(dailySentence.getCategory())
                    .build();
        }

        // 학습 완료 처리
        userSentence.completeLearning();
        userSentence = userSentenceRepository.save(userSentence);

        return new DailySentenceCompleteResponse(
                userSentence.getId(),
                userSentence.getLearningCount(),
                userSentence.getLearnedAt()
        );
    }

    private DailySentenceResponse toResponse(DailySentence dailySentence) {
        return new DailySentenceResponse(
                dailySentence.getId(),
                dailySentence.getSentence(),
                dailySentence.getMeaningKo(),
                dailySentence.getExplanation(),
                dailySentence.getCategory(),
                dailySentence.getExampleDialogue(),
                dailySentence.getDate()
        );
    }
}
