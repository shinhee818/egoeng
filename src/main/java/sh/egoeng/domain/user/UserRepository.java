package sh.egoeng.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.oAuth2Id = :oAuth2Id AND u.oAuth2Provider = :oAuth2Provider")
    Optional<User> findByOAuth2IdAndOAuth2Provider(@Param("oAuth2Id") String oAuth2Id, @Param("oAuth2Provider") String oAuth2Provider);
}


