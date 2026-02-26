package sh.egoeng.security.oauth2.google;

import lombok.Getter;
import org.springframework.stereotype.Service;
import sh.egoeng.feign.oauth2.google.GoogleOAuth2Client;
import sh.egoeng.feign.oauth2.google.GoogleUserInfoClient;
import sh.egoeng.security.oauth2.OAuth2TokenResponse;
import sh.egoeng.security.oauth2.OAuth2UserResponse;
import sh.egoeng.security.oauth2.OAuthServiceInterface;
import sh.egoeng.security.oauth2.config.OAuth2Config;
import sh.egoeng.security.oauth2.config.OAuth2ProviderValues;

@Getter
@Service
public class GoogleAuthService implements OAuthServiceInterface {
    private final OAuth2ProviderValues oAuthInfo;
    private final GoogleOAuth2Client googleOAuth2Client;
    private final GoogleUserInfoClient googleUserInfoClient;

    public GoogleAuthService(OAuth2Config oAuth2Config, GoogleOAuth2Client googleOAuth2Client, GoogleUserInfoClient googleUserInfoClient) {
        if (!oAuth2Config.getProviders().containsKey("google")) {
            throw new IllegalStateException("google provider not found");
        }
        this.oAuthInfo = oAuth2Config.getProviders().get("google");
        this.googleOAuth2Client = googleOAuth2Client;
        this.googleUserInfoClient = googleUserInfoClient;
    }


    @Override
    public String providerName() {
        return "google";
    }

    @Override
    public OAuth2TokenResponse getToken(String code) {
        return googleOAuth2Client.getToken(
                "authorization_code",
                oAuthInfo.clientId(),
                oAuthInfo.clientSecret(),
                code,
                oAuthInfo.redirectUri()
        );
    }

    @Override
    public OAuth2UserResponse getUserInfo(String accessToken) {
        return googleUserInfoClient.getUserInfo(accessToken);
    }
}

