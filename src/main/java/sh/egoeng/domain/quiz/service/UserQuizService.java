package sh.egoeng.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.UserQuiz;
import sh.egoeng.domain.quiz.UserQuizRepository;
import sh.egoeng.domain.user.User;

@Transactional
@Service
@RequiredArgsConstructor
public class UserQuizService {
    private final UserQuizRepository userQuizRepository;

    public UserQuiz startUserQuiz(User user, Quiz quiz) {
        UserQuiz userQuiz = UserQuiz.builder()
                .user(user)
                .quiz(quiz)
                .score(0)
                .retry(0)
                .build();
        
        return save(userQuiz);
    }

    public UserQuiz save(UserQuiz userQuiz) {
        return userQuizRepository.save(userQuiz);
    }
}
