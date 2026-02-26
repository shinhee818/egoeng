package sh.egoeng.feign.oauth2.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "kakaoUserInfoClient",
        url = "https://kapi.kakao.com"
)
public interface KakaoUserInfoClient {

    @GetMapping("/v2/user/me")
    KakaoUserInfoResponse getUserInfo(
            @RequestHeader("Authorization") String accessToken
    );
}

