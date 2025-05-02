package id.ac.ui.cs.advprog.udehnihcourse.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {

    @Test
    void testEnrollmentBuilder() {
        Course course = Course.builder()
                .id(1L)
                .title("Java Programming")
                .build();

        LocalDateTime now = LocalDateTime.now();
        Enrollment enrollment = Enrollment.builder()
                .id(1L)
                .studentId("student123")
                .course(course)
                .enrolledAt(now)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        assertEquals(1L, enrollment.getId());
        assertEquals("student123", enrollment.getStudentId());
        assertEquals(course, enrollment.getCourse());
        assertEquals(now, enrollment.getEnrolledAt());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
    }

    @Test
    void testEnrollmentSettersAndGetters() {
        Enrollment enrollment = new Enrollment();
        Course course = new Course();
        LocalDateTime now = LocalDateTime.now();

        enrollment.setId(1L);
        enrollment.setStudentId("student123");
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(now);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        assertEquals(1L, enrollment.getId());
        assertEquals("student123", enrollment.getStudentId());
        assertEquals(course, enrollment.getCourse());
        assertEquals(now, enrollment.getEnrolledAt());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
    }

    @Test
    void testEnrollmentEqualsAndHashCode() {
        Enrollment enrollment1 = Enrollment.builder()
                .id(1L)
                .studentId("student123")
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .id(1L)
                .studentId("student123")
                .build();

        Enrollment enrollment3 = Enrollment.builder()
                .id(2L)
                .studentId("student123")
                .build();

        assertTrue(enrollment1.equals(enrollment2));
        assertFalse(enrollment1.equals(enrollment3));
        assertEquals(enrollment1.hashCode(), enrollment2.hashCode());
    }

    @Test
    void testEnrollmentToString() {
        LocalDateTime now = LocalDateTime.now();
        Enrollment enrollment = Enrollment.builder()
                .id(1L)
                .studentId("student123")
                .enrolledAt(now)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        String toString = enrollment.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("studentId='student123'"));
        assertTrue(toString.contains("status='ENROLLED'"));
        assertTrue(toString.contains("enrolledAt="));
    }
}