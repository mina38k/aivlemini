package aivlemini.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/*
 * AIService에서 RestTemplate를 bean으로 찾을 수 없는 오류 해결을 위함.
 * restTemplate를 직접 Bean으로 등록.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
