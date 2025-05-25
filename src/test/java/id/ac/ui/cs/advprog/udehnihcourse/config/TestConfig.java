package id.ac.ui.cs.advprog.udehnihcourse.config;

import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public AuthServiceClient mockAuthServiceClient() {
        return mock(AuthServiceClient.class);
    }
}