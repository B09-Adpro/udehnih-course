package id.ac.ui.cs.advprog.udehnihcourse.config;

import feign.Request;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5000, 10000, true); // 5s connect, 10s read
    }
}
