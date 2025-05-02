package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnrolledCourseDTOTest {

    @Test
    void testEnrolledCourseDTOBuilder() {
        EnrolledCourseDTO dto = EnrolledCourseDTO.builder()
                .id(1L)
                .title("Java Programming")
                .instructor("John Doe")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Java Programming", dto.getTitle());
        assertEquals("John Doe", dto.getInstructor());
    }

    @Test
    void testEnrolledCourseDTOSettersAndGetters() {
        EnrolledCourseDTO dto = new EnrolledCourseDTO();
        dto.setId(1L);
        dto.setTitle("Java Programming");
        dto.setInstructor("John Doe");

        assertEquals(1L, dto.getId());
        assertEquals("Java Programming", dto.getTitle());
        assertEquals("John Doe", dto.getInstructor());
    }

    @Test
    void testEnrolledCourseDTOEqualsAndHashCode() {
        EnrolledCourseDTO dto1 = EnrolledCourseDTO.builder()
                .id(1L)
                .title("Java Programming")
                .instructor("John Doe")
                .build();

        EnrolledCourseDTO dto2 = EnrolledCourseDTO.builder()
                .id(1L)
                .title("Java Programming")
                .instructor("John Doe")
                .build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}