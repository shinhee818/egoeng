package sh.egoeng.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.api.auth.dto.TokenResponse;
import sh.egoeng.domain.user.User;
import sh.egoeng.domain.user.service.UserService;
import sh.egoeng.jwt.JwtProvider;
import sh.egoeng.security.oauth2.OAuthServiceInterface;
import sh.egoeng.security.oauth2.OAuth2TokenResponse;
import sh.egoeng.security.oauth2.OAuth2UserResponse;

import java.util.Map;

/**
 * OAuth2 토큰 교환 API
 * 프론트엔드가 OAuth2 code를 받아서 백엔드로 전달하면,
 * 백엔드가 토큰을 교환하고 JWT를 발급하여 반환합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/oauth2")
public class OAuth2ExchangeController {

    private final Map<String, OAuthServiceInterface> oAuthServices;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    /**
     * OAuth2 code를 JWT 토큰으로 교환
     * 
     * @param request code와 state를 포함한 요청
     * @return JWT 토큰 (accessToken, refreshToken) 및 사용자 정보
     */
    @PostMapping("/exchange")
    public ResponseEntity<TokenResponse> exchangeCode(@RequestBody OAuth2ExchangeRequest request) {
        // state에서 provider 추출 (google, kakao 등)
        String provider = extractProviderFromState(request.state());

        OAuthServiceInterface oAuthService = oAuthServices.get(provider + "AuthService");
        if (oAuthService == null) {
            throw new IllegalArgumentException("Unknown OAuth provider: " + provider);
        }

        // 1. OAuth2 토큰 획득
        OAuth2TokenResponse tokenResponse = oAuthService.getToken(request.code());
        String accessToken = tokenResponse.accessToken();

        // 2. 사용자 정보 조회
        OAuth2UserResponse userInfo = oAuthService.getUserInfo(accessToken);

        // 3. 데이터베이스에서 사용자 조회 또는 생성
        User user = userService.findByOAuth2Id(userInfo.id(), provider)
                .orElseGet(() -> {
                    // 새로운 OAuth2 사용자 생성
                    User newUser = User.createOAuth2User(
                            userInfo.email(),
                            userInfo.name(),
                            provider,
                            userInfo.id(),
                            userService.getDefaultRole()
                    );
                    return userService.save(newUser);
                });

        // 4. JWT 토큰 발급
        String jwtAccessToken = jwtProvider.createAccessToken(user.getId(), user.getRole().getName());
        String jwtRefreshToken = jwtProvider.createRefreshToken(user.getId());

        return ResponseEntity.ok(TokenResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getOAuth2Provider())
                .build());
    }

    private String extractProviderFromState(String state) {
        // state 파라미터에서 provider 정보 추출
        // 예: state=google:12345 -> provider는 "google"
        if (state == null || !state.contains(":")) {
            throw new IllegalArgumentException("Invalid state parameter");
        }
        return state.split(":")[0];
    }

    /**
     * OAuth2 토큰 교환 요청 DTO
     */
    public record OAuth2ExchangeRequest(
            String code,   // OAuth2 authorization code
            String state   // OAuth2 state (provider 정보 포함)
    ) {}
}
