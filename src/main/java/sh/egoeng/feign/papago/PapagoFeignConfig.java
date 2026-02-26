package sh.egoeng.feign.papago;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.UUID;

@Configuration
public class PapagoFeignConfig {
    @Value("${papago.client-id}")
    private String clientId;

    @Value("${papago.client-secret}")
    private String clientSecret;

    @Bean
    public RequestInterceptor papagoRequestInterceptor() {
        return template -> {
            template.header("X-NCP-APIGW-API-KEY-ID", clientId);
            template.header("X-NCP-APIGW-API-KEY", clientSecret);
        };
    }
}
