package sh.egoeng.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
    List<UserQuizAnswer> findByQuiz(Quiz quiz);
}

