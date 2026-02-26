package sh.egoeng.feign.oauth2.kakao;

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
public class KakaoUserInfoResponse implements OAuth2UserResponse {

    private String id;

    private String email;

    private String name;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount {
        private String email;
        private String name;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}

