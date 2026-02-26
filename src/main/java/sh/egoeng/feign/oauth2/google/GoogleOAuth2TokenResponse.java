package sh.egoeng.feign.oauth2.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sh.egoeng.security.oauth2.OAuth2TokenResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOAuth2TokenResponse implements OAuth2TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String scope;

    @JsonProperty("id_token")
    private String idToken;

    @Override
    public String accessToken() {
        return this.accessToken;
    }
}

