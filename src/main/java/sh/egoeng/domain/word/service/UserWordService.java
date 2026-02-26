package sh.egoeng.domain.word.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.domain.word.event.UserWordAddedEvent;
import sh.egoeng.domain.word.event.DailyWordCountEvent;
import sh.egoeng.domain.word.exception.UserWordAlreadyExistsException;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserWordService {
    private final UserWordRepository userWordRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserWord registerUserWord(UserWord userWord) {
        if (exist(userWord)) {
            if (userWord.isCustomWord()) {
                throw new IllegalArgumentException("이미 등록된 커스텀 단어입니다: " + userWord.getCustomText());
            } else {
                throw new UserWordAlreadyExistsException(userWord.getWord().getId());
            }
        }

        UserWord savedUserWord = userWordRepository.save(userWord);
        Long userId = savedUserWord.getUser().getId();

        // 1. 단어 추가 이벤트 발행
        eventPublisher.publishEvent(new UserWordAddedEvent(savedUserWord, userId));
        log.info("✅ UserWordAddedEvent 발행: userId={}, word={}", userId,
                 savedUserWord.isCustomWord() ? savedUserWord.getCustomText() : savedUserWord.getWord().getText());

        // 2. 오늘 등록한 단어 개수 확인
        long todayCount = userWordRepository.countByUserIdAndCreatedAtIsToday(userId);
        log.info("📊 오늘 등록한 단어 개수: userId={}, count={}", userId, todayCount);

        // 3. 3개 이상마다 알림 발송 (3, 6, 9, 12, ...)
        if (todayCount % 3 == 0) {
            log.info("🔔 DailyWordCountEvent 발행: userId={}, count={}, 3의 배수={}",
                     userId, todayCount, todayCount % 3 == 0);
            eventPublisher.publishEvent(
                new DailyWordCountEvent(userId, todayCount)
            );
        } else {
            log.info("⏭️ 아직 3의 배수 아님: count={}, count%3={}", todayCount, todayCount % 3);
        }

        return savedUserWord;
    }

    @Transactional(readOnly = true)
    public boolean exist(UserWord userWord) {
        if (userWord.isCustomWord()) {
            // 커스텀 단어 중복 체크: 같은 user, 같은 customText, 같은 customMeaningKo
            return userWordRepository.existsByUserAndCustomTextAndCustomMeaningKo(
                    userWord.getUser(),
                    userWord.getCustomText(),
                    userWord.getCustomMeaningKo()
            );
        } else {
            // 기존 Word 참조 단어 중복 체크
            return userWordRepository.existsUserWordByUserAndWord(userWord.getUser(), userWord.getWord());
        }
    }

    @Transactional(readOnly = true)
    public Optional<UserWord> findByUserIdAndWordId(Long userId, Long wordId) {
        return userWordRepository.findByUserIdAndWordId(userId, wordId);
    }

    public void delete(UserWord userWord) {
        userWordRepository.delete(userWord);
    }

    @Transactional(readOnly = true)
    public Optional<UserWord> findById(Long userWordId) {
        return userWordRepository.findById(userWordId);
    }

    /**
     * 오늘 등록한 단어 개수
     */
    @Transactional(readOnly = true)
    public long getTodayWordCount(Long userId) {
        return userWordRepository.countByUserIdAndCreatedAtIsToday(userId);
    }
}