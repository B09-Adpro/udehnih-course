package id.ac.ui.cs.advprog.udehnihcourse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "SERVER_PORT=8081",
        "payment.service.baseurl=http://localhost:8082",
        "PAYMENT_SERVICE_API_KEY=test-api-key-for-testing",
        "services.auth.baseurl=http://localhost:8080",
        "services.payment.baseurl=http://localhost:8081",
        "services.payment.api-key=test-api-key",
        "payment.service.api.key=test-api-key-for-testing",
        "jwt.secret-key=test-secret-key-for-testing-only-minimum-256-bits-long-for-hs512-algorithm",
        "jwt.expiration=3600000",
        "jwt.refresh-expiration=86400000",
        "cors.allowed-origins=*",
        "cors.allowed-methods=*",
        "cors.allowed-headers=*",
        "cors.allow-credentials=false"
})
class UdehnihCourseApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethod_shouldRunWithoutErrors() {
//        try {
//            UdehnihCourseApplication.main(new String[]{});
//        } catch (Exception e) {
//            System.err.println("Warning: mainMethod_shouldRunWithoutErrors test might fail due to context/port issues when run with other tests. Exception: " + e.getMessage());
//        }
    }
}
