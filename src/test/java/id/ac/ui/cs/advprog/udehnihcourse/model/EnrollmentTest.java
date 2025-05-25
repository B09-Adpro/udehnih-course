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
                .studentId(123L)
                .course(course)
                .enrolledAt(now)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        assertEquals(1L, enrollment.getId());
        assertEquals(123L, enrollment.getStudentId());
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
        enrollment.setStudentId(123L);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(now);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);

        assertEquals(1L, enrollment.getId());
        assertEquals(123L, enrollment.getStudentId());
        assertEquals(course, enrollment.getCourse());
        assertEquals(now, enrollment.getEnrolledAt());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
    }

    @Test
    void testEnrollmentEqualsAndHashCode() {
        Enrollment enrollment1 = Enrollment.builder()
                .id(1L)
                .studentId(123L)
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .id(1L)
                .studentId(123L)
                .build();

        Enrollment enrollment3 = Enrollment.builder()
                .id(2L)
                .studentId(123L)
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
                .studentId(123L)
                .enrolledAt(now)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        String toString = enrollment.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("studentId='123'"));
        assertTrue(toString.contains("status='ENROLLED'"));
        assertTrue(toString.contains("enrolledAt="));
    }
}