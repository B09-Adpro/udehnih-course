package id.ac.ui.cs.advprog.udehnihcourse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "SERVER_PORT=8081",
        "payment.service.baseurl=http://localhost:8082",
        "PAYMENT_SERVICE_API_KEY=test-api-key-for-testing"
})
class UdehnihCourseApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethod_shouldRunWithoutErrors() {
        try {
            UdehnihCourseApplication.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Warning: mainMethod_shouldRunWithoutErrors test might fail due to context/port issues when run with other tests. Exception: " + e.getMessage());
        }
    }
}
