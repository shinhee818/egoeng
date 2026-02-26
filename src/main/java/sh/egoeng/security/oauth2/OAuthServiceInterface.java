package sh.egoeng.security.oauth2;


public interface OAuthServiceInterface {
    String providerName();
    OAuth2TokenResponse getToken(String code);
    OAuth2UserResponse getUserInfo(String accessToken);
}
