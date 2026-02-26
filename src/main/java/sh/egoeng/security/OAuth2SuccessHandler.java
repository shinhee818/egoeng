package sh.egoeng.security;

// 이 클래스는 더 이상 사용되지 않습니다.
// 우리는 커스텀 OAuth2 구현(Feign 클라이언트 + OAuth2CallbackController)을 사용합니다.
// @Deprecated

// 원본 코드:
/*
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.UserRepository;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.jwt.JwtProvider;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String provider = (String) authentication.getDetails();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.createOAuth2User(
                            email,
                            name,
                            provider != null ? provider : "oauth2",
                            oAuth2User.getName(),
                            userService.getDefaultRole()
                    );
                    return userRepository.save(newUser);
                });

        String accessToken =
                jwtProvider.createAccessToken(user.getId(), user.getRole().getName());

        String refreshToken =
                jwtProvider.createRefreshToken(user.getId());

        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "accessToken": "%s",
              "refreshToken": "%s"
            }
            """.formatted(accessToken, refreshToken));
    }
}
*/

