package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class CourseListDTOTest {
    @Test
    void testCourseListDTOBuilderAndGetters() {
        CourseListDTO courseListDTO = CourseListDTO.builder()
                .id(1L)
                .title("Java Programming")
                .price(new BigDecimal("100.00"))
                .instructor("John Doe") // Updated field name
                .category("Programming") // Added field
                .build();

        assertEquals(1L, courseListDTO.getId());
        assertEquals("Java Programming", courseListDTO.getTitle());
        assertEquals(new BigDecimal("100.00"), courseListDTO.getPrice());
        assertEquals("John Doe", courseListDTO.getInstructor()); // Updated field name
        assertEquals("Programming", courseListDTO.getCategory()); // Added field
    }
    
}
