package sh.egoeng.api.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.api.exception.UserNotFoundException;
import sh.egoeng.api.quiz.controller.dto.request.generate.GenerateWordQuizRequest;
import sh.egoeng.api.quiz.controller.dto.response.GenerateWordQuizResponse;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.service.QuizService;
import sh.egoeng.domain.quiz.service.UserQuizService;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.api.quiz.service.wordquiz.WordQueryBuilder;
import sh.egoeng.api.quiz.service.wordquiz.WordQuizItemGenerator;
import sh.egoeng.domain.word.Word;
import sh.egoeng.domain.word.WordMeaning;
import sh.egoeng.domain.word.WordMeaningRepository;
import sh.egoeng.domain.word.WordRepository;
import sh.egoeng.domain.word.CategoryRepository;
import sh.egoeng.domain.word.CategoryWordRepository;
import sh.egoeng.security.SecurityUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerateWordQuizService {
    private final UserService userService;
    private final WordRepository wordRepository;
    private final WordMeaningRepository wordMeaningRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryWordRepository categoryWordRepository;
    private final QuizService quizService;
    private final UserQuizService userQuizService;

    public GenerateWordQuizResponse generateWordQuiz(GenerateWordQuizRequest request) {
        Long userId = SecurityUtils.currentId();
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        int limit = request.limit() != null ? request.limit() : 20;

        // Builder 패턴으로 단어 필터링
        WordQueryBuilder queryBuilder = new WordQueryBuilder(
                wordRepository,
                categoryRepository,
                categoryWordRepository
        );

        List<Word> words = queryBuilder
                .byCategory(request.category())
                .byLevel(request.level())
                .build();

        if (words.isEmpty()) {
            throw new IllegalArgumentException("No words found with the specified criteria.");
        }

        // 랜덤으로 limit 개수만큼 선택
        java.util.Collections.shuffle(words);
        if (words.size() > limit) {
            words = words.subList(0, limit);
        }

        // WordMeaning 초기화 (LAZY loading)
        words.forEach(word -> word.getMeanings().size());

        // 모든 단어의 의미를 가져와서 오답으로 사용할 풀 생성
        List<WordMeaning> allMeanings = wordMeaningRepository.findAll();
        List<String> allMeaningTexts = allMeanings.stream()
                .map(WordMeaning::getMeaningKo)
                .filter(meaning -> meaning != null && !meaning.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        // 퀴즈 아이템 생성기 초기화
        WordQuizItemGenerator generator = new WordQuizItemGenerator(allMeaningTexts);

        // 각 단어로부터 퀴즈 아이템 생성
        List<Map<String, Object>> wordAnswers = new ArrayList<>();
        List<GenerateWordQuizResponse.WordQuizItem> wordItems = words.stream()
                .map(generator::generateQuizItem)
                .map(result -> {
                    wordAnswers.add(result.toAnswerMap());
                    return result.toResponseItem();
                })
                .collect(Collectors.toList());

        // Word ID 목록 추출
        List<Long> wordIds = words.stream()
                .map(Word::getId)
                .collect(Collectors.toList());

        // Quiz question JSONB 생성 (정답 정보 포함)
        Map<String, Object> questionMap = new HashMap<>();
        questionMap.put("wordIds", wordIds);
        questionMap.put("wordAnswers", wordAnswers);  // 각 wordId별 correctAnswerIndex
        questionMap.put("category", request.category());
        questionMap.put("level", request.level());

        // Quiz 엔티티 생성
        Quiz quiz = quizService.createQuiz(
                QuizType.WORD_QUIZ,
                "Word Quiz",
                questionMap
        );

        // UserQuiz 생성
        userQuizService.startUserQuiz(user, quiz);

        return new GenerateWordQuizResponse(
                quiz.getId(),
                request.category(),
                request.level(),
                wordItems
        );
    }
}

