package id.ac.ui.cs.advprog.udehnihcourse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        if (allowedOrigins.size() == 1 && "*".equals(allowedOrigins.get(0))) {
            configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        } else {
            configuration.setAllowedOrigins(allowedOrigins);
        }

        if (allowedMethods.size() == 1 && "*".equals(allowedMethods.get(0))) {
            configuration.setAllowedMethods(Collections.singletonList("*"));
        } else {
            configuration.setAllowedMethods(allowedMethods);
        }

        if (allowedHeaders.size() == 1 && "*".equals(allowedHeaders.get(0))) {
            configuration.setAllowedHeaders(Collections.singletonList("*"));
        } else {
            configuration.setAllowedHeaders(allowedHeaders);
        }

        configuration.setAllowCredentials(allowCredentials);

        configuration.setExposedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Location"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}