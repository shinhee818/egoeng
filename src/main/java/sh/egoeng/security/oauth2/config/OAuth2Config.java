package sh.egoeng.security.oauth2.config;

import com.querydsl.core.annotations.Config;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Config {
    private final Map<String, OAuth2ProviderValues> providers = new HashMap<>();
}
