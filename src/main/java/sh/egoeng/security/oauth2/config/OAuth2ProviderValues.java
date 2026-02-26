package sh.egoeng.security.oauth2.config;

public record OAuth2ProviderValues(
    String clientId,
    String clientSecret,
    String redirectUri,
    String tokenUrl
) {
}
