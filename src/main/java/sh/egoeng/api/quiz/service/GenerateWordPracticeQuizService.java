package sh.egoeng.api.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.quiz.controller.dto.request.generate.GenerateWordPracticeQuizRequest;
import sh.egoeng.api.quiz.controller.dto.response.GenerateWordPracticeQuizResponse;
import sh.egoeng.api.quiz.controller.dto.response.WordPracticeQuizItem;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.UserQuiz;
import sh.egoeng.domain.quiz.service.QuizService;
import sh.egoeng.domain.quiz.service.UserQuizService;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.domain.word.UserWord;
import sh.egoeng.domain.word.UserWordRepository;
import sh.egoeng.security.SecurityUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerateWordPracticeQuizService {

    private final UserService userService;
    private final UserWordRepository userWordRepository;
    private final QuizService quizService;
    private final UserQuizService userQuizService;

    public GenerateWordPracticeQuizResponse generateWordPracticeQuiz(GenerateWordPracticeQuizRequest request) {
        Long userId = SecurityUtils.currentId();
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String mode = request.mode() != null ? request.mode() : "WORD_HIDE";
        int limit = request.limit() != null ? request.limit() : 20;

        // 유저의 단어 목록 가져오기 (JOIN FETCH로 Word 포함)
        List<UserWord> userWords = userWordRepository.findByUserIdAndCursor(
                userId,
                null, // cursor 없이 처음부터
                PageRequest.of(0, limit)
        );

        // WordMeaning 초기화 (LAZY loading - N+1 방지를 위해 미리 초기화)
        userWords.forEach(userWord -> {
            if (userWord.getWord() != null) {
                userWord.getWord().getMeanings().size(); // LAZY loading trigger
            }
        });

        if (userWords.isEmpty()) {
            throw new IllegalArgumentException("학습할 단어가 없습니다.");
        }

        // UserWord ID 목록 추출
        List<Long> userWordIds = userWords.stream()
                .map(UserWord::getId)
                .collect(Collectors.toList());

        // Quiz question JSONB 생성
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("userWordIds", userWordIds);
        questionMap.put("mode", mode);

        // Quiz 엔티티 생성
        Quiz quiz = quizService.createQuiz(
                QuizType.WORD_PRACTICE,
                "Word Practice Quiz",
                questionMap
        );

        // UserQuiz 생성 (사용하지 않지만 기존 구조 유지를 위해 생성)
        userQuizService.startUserQuiz(user, quiz);

        // Response용 WordPracticeQuizItem 리스트 생성
        List<WordPracticeQuizItem> wordItems = userWords.stream()
                .map(userWord -> {
                    List<WordPracticeQuizItem.WordMeaning> meanings = userWord.getWord().getMeanings().stream()
                            .map(meaning -> new WordPracticeQuizItem.WordMeaning(
                                    meaning.getId(),
                                    meaning.getMeaningKo(),
                                    meaning.getPartOfSpeech(),
                                    meaning.getExample()
                            ))
                            .collect(Collectors.toList());

                    return new WordPracticeQuizItem(
                            userWord.getId(),
                            userWord.getWord().getText(),
                            meanings,
                            userWord.getPracticeCount() != null ? userWord.getPracticeCount() : 0,
                            userWord.getLastPracticedAt()
                    );
                })
                .collect(Collectors.toList());

        return new GenerateWordPracticeQuizResponse(
                quiz.getId(),
                mode,
                wordItems
        );
    }
}

