package sh.egoeng.feign.oauth2;

import sh.egoeng.security.oauth2.OAuth2TokenResponse;

public interface OAuth2Client {
    OAuth2TokenResponse getToken(
            String grantType,
            String clientId,
            String clientSecret,
            String code,
            String redirectUri
    );
}