package sh.egoeng.feign.oauth2.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sh.egoeng.security.oauth2.OAuth2UserResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfoResponse implements OAuth2UserResponse {

    private String id;

    private String email;

    private String name;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("verified_email")
    private Boolean verifiedEmail;

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String email() {
        return this.email;
    }

    @Override
    public String name() {
        return this.name;
    }
}

