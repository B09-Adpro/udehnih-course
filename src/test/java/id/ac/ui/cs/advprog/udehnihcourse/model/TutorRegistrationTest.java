package id.ac.ui.cs.advprog.udehnihcourse.model;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class TutorRegistrationTest {
    @Test
    void testTutorRegistrationCreationAndGetters() {
        String studentId = "student-test";
        String exp = "5 years teaching";
        String qual = "PhD";
        String bio = "Passionate educator";
        LocalDateTime now = LocalDateTime.now();

        TutorRegistration app = new TutorRegistration(studentId, exp, qual, bio);
        app.setId(1L);
        app.setSubmittedAt(now);
        app.setStatus(TutorRegistrationStatus.PENDING);

        assertEquals(1L, app.getId());
        assertEquals(studentId, app.getStudentId());
        assertEquals(exp, app.getExperience());
        assertEquals(qual, app.getQualifications());
        assertEquals(bio, app.getBio());
        assertEquals(TutorRegistrationStatus.PENDING, app.getStatus());
        assertEquals(now, app.getSubmittedAt());
        assertNull(app.getProcessedAt());
    }
}
