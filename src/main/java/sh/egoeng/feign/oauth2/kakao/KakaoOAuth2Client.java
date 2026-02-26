package sh.egoeng.feign.oauth2.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sh.egoeng.feign.oauth2.OAuth2Client;

@FeignClient(
        name = "kakaoOAuth2Client",
        url = "${oauth2.providers.kakao.token-url:https://kauth.kakao.com/oauth/token}"
)
public interface KakaoOAuth2Client extends OAuth2Client {

    @PostMapping
    KakaoOAuth2TokenResponse getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri
    );
}