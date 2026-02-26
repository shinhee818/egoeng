package sh.egoeng.feign.oauth2.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "googleUserInfoClient",
        url = "https://www.googleapis.com/oauth2/v2"
)
public interface GoogleUserInfoClient {

    @GetMapping("/userinfo")
    GoogleUserInfoResponse getUserInfo(
            @RequestParam("access_token") String accessToken
    );
}

