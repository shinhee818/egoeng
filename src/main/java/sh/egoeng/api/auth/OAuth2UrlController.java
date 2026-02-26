package sh.egoeng.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.egoeng.security.oauth2.config.OAuth2Config;
import sh.egoeng.security.oauth2.config.OAuth2ProviderValues;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/oauth2")
public class OAuth2UrlController {

    private final OAuth2Config oAuth2Config;

    @GetMapping("/google-login-url")
    public Map<String, String> getGoogleLoginUrl() {
        OAuth2ProviderValues googleConfig = oAuth2Config.getProviders().get("google");

        if (googleConfig == null) {
            throw new IllegalStateException("Google OAuth2 configuration not found");
        }

        // state 파라미터 생성 (CSRF 방지)
        String state = "google:" + System.currentTimeMillis() + "_" +
                      java.util.UUID.randomUUID().toString().substring(0, 8);

        // Google OAuth2 로그인 URL 생성
        // redirect_uri는 백엔드 설정값 사용 (프론트엔드 URL로 설정됨)
        String redirectUri = googleConfig.redirectUri();
        System.out.println("🔍 [OAuth2] Google redirect_uri: " + redirectUri);
        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        String googleLoginUrl = String.format(
            "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=openid%%20email%%20profile&state=%s",
            googleConfig.clientId(),
            encodedRedirectUri,
            state
        );

        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", googleLoginUrl);
        response.put("state", state);

        return response;
    }

    @GetMapping("/kakao-login-url")
    public Map<String, String> getKakaoLoginUrl() {
        OAuth2ProviderValues kakaoConfig = oAuth2Config.getProviders().get("kakao");

        if (kakaoConfig == null) {
            throw new IllegalStateException("Kakao OAuth2 configuration not found");
        }

        // state 파라미터 생성 (CSRF 방지)
        String state = "kakao:" + System.currentTimeMillis() + "_" +
                      java.util.UUID.randomUUID().toString().substring(0, 8);

        // Kakao OAuth2 로그인 URL 생성
        // redirect_uri는 백엔드 설정값 사용 (프론트엔드 URL로 설정됨)
        String redirectUri = URLEncoder.encode(kakaoConfig.redirectUri(), StandardCharsets.UTF_8);
        String kakaoLoginUrl = String.format(
            "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&state=%s",
            kakaoConfig.clientId(),
            redirectUri,
            state
        );

        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", kakaoLoginUrl);
        response.put("state", state);

        return response;
    }
}

