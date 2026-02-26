package sh.egoeng.api.word.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.word.controller.dto.response.UserWordPracticeResponse;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.security.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 유저 단어 학습 서비스
 * 퀴즈와 별개로 단순히 단어를 학습하는 기능
 */
@Service
@RequiredArgsConstructor
public class UserWordPracticeService {

    private final UserService userService;
    private final UserWordRepository userWordRepository;

    /**
     * 유저 단어장에서 랜덤으로 단어를 선택하여 학습 시작
     *
     * @param limit 가져올 단어 개수 (기본값: 20)
     * @return 학습할 단어 목록
     */
    public UserWordPracticeResponse startPractice(Integer limit) {
        Long userId = SecurityUtils.currentId();
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        int wordLimit = limit != null && limit > 0 ? limit : 20;

        // 유저의 모든 단어 가져오기 (랜덤 선택을 위해)
        List<UserWord> allUserWords = userWordRepository.findAllByUserIdWithWord(userId);

        if (allUserWords.isEmpty()) {
            throw new IllegalArgumentException("학습할 단어가 없습니다.");
        }

        // WordMeaning 초기화 (LAZY loading) - 커스텀 단어는 제외
        allUserWords.forEach(userWord -> {
            if (userWord.getWord() != null) {
                userWord.getWord().getMeanings().size();
            }
        });

        // 랜덤으로 limit 개수만큼 선택
        Collections.shuffle(allUserWords);
        List<UserWord> selectedWords = allUserWords.size() > wordLimit
                ? allUserWords.subList(0, wordLimit)
                : allUserWords;

        // Response 변환
        List<UserWordPracticeResponse.UserWordPracticeItem> wordItems = selectedWords.stream()
                .map(userWord -> {
                    List<UserWordPracticeResponse.UserWordPracticeItem.WordMeaning> meanings;
                    
                    if (userWord.isCustomWord()) {
                        // 커스텀 단어: customMeaningKo를 WordMeaning으로 변환
                        meanings = List.of(
                                new UserWordPracticeResponse.UserWordPracticeItem.WordMeaning(
                                        null, // 커스텀 단어는 WordMeaning ID가 없음
                                        userWord.getMeaningKo(),
                                        null, // 품사 정보 없음
                                        null  // 예문 정보 없음
                                )
                        );
                    } else {
                        // 기존 Word 참조
                        meanings = userWord.getWord().getMeanings().stream()
                                .map(meaning -> new UserWordPracticeResponse.UserWordPracticeItem.WordMeaning(
                                        meaning.getId(),
                                        meaning.getMeaningKo(),
                                        meaning.getPartOfSpeech(),
                                        meaning.getExample()
                                ))
                                .collect(Collectors.toList());
                    }

                    return new UserWordPracticeResponse.UserWordPracticeItem(
                            userWord.getId(),
                            userWord.getText(), // 헬퍼 메서드 사용
                            meanings,
                            userWord.getPracticeCount() != null ? userWord.getPracticeCount() : 0,
                            userWord.getLastPracticedAt()
                    );
                })
                .collect(Collectors.toList());

        return new UserWordPracticeResponse(wordItems);
    }

    /**
     * 단어 학습 완료 (practiceCount, lastPracticedAt 업데이트)
     *
     * @param userId 유저 ID
     * @param userWordId 유저 단어 ID
     */
    @Transactional
    public void completePractice(Long userId, Long userWordId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserWord userWord = userWordRepository.findById(userWordId)
                .orElseThrow(() -> new IllegalArgumentException("UserWord not found: " + userWordId));

        if (!userWord.getUser().getId().equals(userId)) {
            throw new SecurityException("User does not have permission to complete practice for this word.");
        }

        userWord.completePractice();
        userWordRepository.save(userWord);
    }
}

