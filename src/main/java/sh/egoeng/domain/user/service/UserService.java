package sh.egoeng.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.egoeng.domain.role.Role;
import sh.egoeng.domain.role.RoleRepository;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.UserRepository;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByOAuth2Id(String oAuth2Id, String provider) {
        return userRepository.findByOAuth2IdAndOAuth2Provider(oAuth2Id, provider);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Role getDefaultRole() {
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role not found"));
    }

    // 회원가입
    public User signUp(String email, String password, String name) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 생성
        User newUser = User.createLocalUser(
                email,
                name,
                encodedPassword,
                getDefaultRole()
        );

        return save(newUser);
    }

    // 이메일 중복 확인
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}


