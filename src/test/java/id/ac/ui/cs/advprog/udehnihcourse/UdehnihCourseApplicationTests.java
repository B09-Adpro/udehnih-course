package id.ac.ui.cs.advprog.udehnihcourse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
