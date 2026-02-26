package sh.egoeng.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizRepository;
import sh.egoeng.domain.quiz.UserQuizAnswer;
import sh.egoeng.domain.quiz.UserQuizAnswerRepository;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class UserQuizAnswerService {
    private final UserQuizAnswerRepository userQuizAnswerRepository;
    private final QuizRepository quizRepository;
    private final UserService userService;

    public UserQuizAnswer saveAnswer(Long quizId, Long userId, String answer, boolean isCorrect, List<Boolean> perBlankCorrect) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        LocalDateTime now = LocalDateTime.now();
        
        UserQuizAnswer userQuizAnswer = UserQuizAnswer.builder()
                .quiz(quiz)
                .user(user)
                .answer(answer)
                .isCorrect(isCorrect)
                .perBlankCorrect(perBlankCorrect)
                .answeredAt(now)
                .regDt(now)
                .build();

        return userQuizAnswerRepository.save(userQuizAnswer);
    }
}

