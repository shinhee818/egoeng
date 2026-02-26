package sh.egoeng.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.quiz.Quiz;
import sh.egoeng.domain.quiz.QuizRepository;
import sh.egoeng.domain.quiz.QuizType;

import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

    public Quiz createQuiz(QuizType type, String title, Map<String, Object> question) {
        Quiz quiz = Quiz.builder()
                .type(type)
                .title(title)
                .question(question)
                .build();
        
        return quizRepository.save(quiz);
    }
}

















