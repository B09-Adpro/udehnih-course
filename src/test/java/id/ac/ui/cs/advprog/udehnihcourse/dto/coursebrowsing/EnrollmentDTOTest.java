package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentDTOTest {

    @Test
    void testEnrollmentDTOBuilder() {
        EnrollmentDTO dto = EnrollmentDTO.builder()
                .enrollmentId(1L)
                .courseTitle("Java Programming")
                .status("ENROLLED")
                .message("Successfully enrolled")
                .enrolledAt("2024-03-15T10:00:00")
                .build();

        assertEquals(1L, dto.getEnrollmentId());
        assertEquals("Java Programming", dto.getCourseTitle());
        assertEquals("ENROLLED", dto.getStatus());
        assertEquals("Successfully enrolled", dto.getMessage());
        assertEquals("2024-03-15T10:00:00", dto.getEnrolledAt());
    }

    @Test
    void testEnrollmentDTOSettersAndGetters() {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setEnrollmentId(1L);
        dto.setCourseTitle("Java Programming");
        dto.setStatus("ENROLLED");
        dto.setMessage("Successfully enrolled");
        dto.setEnrolledAt("2024-03-15T10:00:00");

        assertEquals(1L, dto.getEnrollmentId());
        assertEquals("Java Programming", dto.getCourseTitle());
        assertEquals("ENROLLED", dto.getStatus());
        assertEquals("Successfully enrolled", dto.getMessage());
        assertEquals("2024-03-15T10:00:00", dto.getEnrolledAt());
    }

    @Test
    void testEnrollmentDTOEqualsAndHashCode() {
        EnrollmentDTO dto1 = EnrollmentDTO.builder()
                .enrollmentId(1L)
                .courseTitle("Java Programming")
                .status("ENROLLED")
                .message("Successfully enrolled")
                .enrolledAt("2024-03-15T10:00:00")
                .build();

        EnrollmentDTO dto2 = EnrollmentDTO.builder()
                .enrollmentId(1L)
                .courseTitle("Java Programming")
                .status("ENROLLED")
                .message("Successfully enrolled")
                .enrolledAt("2024-03-15T10:00:00")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}