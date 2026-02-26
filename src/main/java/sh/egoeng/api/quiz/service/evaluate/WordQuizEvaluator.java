package sh.egoeng.api.quiz.service.evaluate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sh.egoeng.api.quiz.controller.dto.response.WordQuizEvaluationResponse;
import sh.egoeng.api.quiz.controller.dto.response.BaseEvaluationResponse;
import sh.egoeng.api.quiz.service.dto.WordQuizEvaluateRequest;
import sh.egoeng.api.quiz.service.llm.dto.QuizEvaluateRequest;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizRepository;
import sh.egoeng.domain.quiz.QuizType;
import sh.egoeng.domain.quiz.UserQuiz;
import sh.egoeng.domain.quiz.UserQuizRepository;
import sh.egoeng.domain.quiz.service.UserQuizAnswerService;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.security.SecurityUtils;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WordQuizEvaluator implements QuizEvaluator {
    private final QuizRepository quizRepository;
    private final UserQuizRepository userQuizRepository;
    private final UserService userService;
    private final UserQuizAnswerService userQuizAnswerService;

    @Override
    public QuizType getType() {
        return QuizType.WORD_QUIZ;
    }

    @Override
    public BaseEvaluationResponse evaluate(QuizEvaluateRequest req) {
        WordQuizEvaluateRequest wordQuizReq = (WordQuizEvaluateRequest) req;
        Long userId = SecurityUtils.currentId();

        Quiz quiz = quizRepository.findById(wordQuizReq.quizId())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + wordQuizReq.quizId()));

        Map<String, Object> questionMap = quiz.getQuestion();
        List<Map<String, Object>> wordAnswers = (List<Map<String, Object>>) questionMap.get("wordAnswers");

        // 각 답안을 개별적으로 저장
        int correctCount = 0;
        for (WordQuizEvaluateRequest.WordAnswer answer : wordQuizReq.answers()) {
            // 해당 wordId의 정답 인덱스 찾기
            Integer correctAnswerIndex = wordAnswers.stream()
                    .filter(wa -> {
                        Object wordIdObj = wa.get("wordId");
                        Long wordId = wordIdObj instanceof Integer 
                                ? ((Integer) wordIdObj).longValue() 
                                : (Long) wordIdObj;
                        return wordId.equals(answer.wordId());
                    })
                    .map(wa -> {
                        Object indexObj = wa.get("correctAnswerIndex");
                        return indexObj instanceof Integer 
                                ? (Integer) indexObj 
                                : ((Number) indexObj).intValue();
                    })
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Word ID not found in quiz: " + answer.wordId()));

            // 정답 여부 확인
            boolean isCorrect = answer.selectedIndex().equals(correctAnswerIndex);
            if (isCorrect) {
                correctCount++;
            }

            // UserQuizAnswer에 개별 저장
            // answer 필드에 "wordId:selectedIndex" 형식으로 저장하여 wordId 정보 보존
            String answerValue = answer.wordId() + ":" + answer.selectedIndex();
            userQuizAnswerService.saveAnswer(
                    quiz.getId(),
                    userId,
                    answerValue,  // "wordId:selectedIndex" 형식으로 저장
                    isCorrect,
                    null  // perBlankCorrect는 빈칸 퀴즈 전용
            );
        }

        // UserQuiz의 score 업데이트 (퀴즈 세트 전체 점수)
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        UserQuiz userQuiz = userQuizRepository.findByUserAndQuiz(user, quiz)
                .orElseThrow(() -> new IllegalArgumentException("UserQuiz not found for user: " + userId + ", quiz: " + quiz.getId()));
        userQuiz.updateScore(correctCount);
        userQuizRepository.save(userQuiz);

        // 전체 평가 결과 반환 (개별 답안은 이미 저장됨)
        int totalCount = wordQuizReq.answers().size();
        return new WordQuizEvaluationResponse(
                quiz.getId(),
                correctCount == totalCount,
                String.format("정답률: %d/%d (%.1f%%)", correctCount, totalCount, (correctCount * 100.0 / totalCount))
        );
    }
}

