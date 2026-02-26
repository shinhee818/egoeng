package sh.egoeng.feign.oauth2.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sh.egoeng.feign.oauth2.OAuth2Client;

@FeignClient(
        name = "googleOAuth2Client",
        url = "${oauth2.providers.google.token-url:https://oauth2.googleapis.com/token}"
)
public interface GoogleOAuth2Client extends OAuth2Client {

    @PostMapping
    GoogleOAuth2TokenResponse getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri
    );
}

