package sh.egoeng.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import sh.egoeng.api.auth.dto.*;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.jwt.JwtProvider;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @PostMapping("/signup")
    public SignUpResponse signUp(@RequestBody SignUpRequest request) {
        // 비밀번호 확인
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 유효성 검사
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8글자 이상이어야 합니다.");
        }

        // 회원가입 처리
        User newUser = userService.signUp(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );

        return SignUpResponse.builder()
                .userId(newUser.getId())
                .email(newUser.getEmail())
                .name(newUser.getName())
                .message("회원가입이 완료되었습니다.")
                .build();
    }

    @GetMapping("/check-email")
    public CheckEmailResponse checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        return CheckEmailResponse.builder()
                .email(email)
                .exists(exists)
                .available(!exists)
                .build();
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken =
                jwtProvider.createAccessToken(user.getId(), user.getRole().getName());

        String refreshToken =
                jwtProvider.createRefreshToken(user.getId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getOAuth2Provider())
                .build();
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtProvider.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccessToken =
                jwtProvider.createAccessToken(user.getId(), user.getRole().getName());

        String newRefreshToken =
                jwtProvider.createRefreshToken(user.getId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getOAuth2Provider())
                .build();
    }
}


