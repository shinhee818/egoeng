package sh.egoeng.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import sh.egoeng.domain.user.User;

import java.util.Optional;

public interface UserQuizRepository extends JpaRepository<UserQuiz, Long> {
    Optional<UserQuiz> findByUserAndQuiz(User user, Quiz quiz);
}
