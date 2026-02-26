package sh.egoeng.security.oauth2.kakao;

import lombok.Getter;
import org.springframework.stereotype.Service;
import sh.egoeng.feign.oauth2.kakao.KakaoOAuth2Client;
import sh.egoeng.feign.oauth2.kakao.KakaoUserInfoClient;
import sh.egoeng.security.oauth2.OAuth2TokenResponse;
import sh.egoeng.security.oauth2.OAuth2UserResponse;
import sh.egoeng.security.oauth2.OAuthServiceInterface;
import sh.egoeng.security.oauth2.config.OAuth2Config;
import sh.egoeng.security.oauth2.config.OAuth2ProviderValues;

@Getter
@Service
public class KakaoAuthService implements OAuthServiceInterface {
    private final OAuth2ProviderValues oAuthInfo;
    private final KakaoOAuth2Client kakaoOAuth2Client;
    private final KakaoUserInfoClient kakaoUserInfoClient;

    public KakaoAuthService(OAuth2Config oAuth2Config, KakaoOAuth2Client kakaoOAuth2Client, KakaoUserInfoClient kakaoUserInfoClient) {
        if (!oAuth2Config.getProviders().containsKey("kakao")) {
            throw new IllegalStateException("kakao provider not found");
        }
        this.oAuthInfo = oAuth2Config.getProviders().get("kakao");
        this.kakaoOAuth2Client = kakaoOAuth2Client;
        this.kakaoUserInfoClient = kakaoUserInfoClient;
    }


    @Override
    public String providerName() {
        return "kakao";
    }

    @Override
    public OAuth2TokenResponse getToken(String code) {
        return kakaoOAuth2Client.getToken(
                "authorization_code",
                oAuthInfo.clientId(),
                oAuthInfo.clientSecret(),
                code,
                oAuthInfo.redirectUri()
        );
    }

    @Override
    public OAuth2UserResponse getUserInfo(String accessToken) {
        return kakaoUserInfoClient.getUserInfo("Bearer " + accessToken);
    }
}



