package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CourseDetailDTOTest {
    @Test
    void testCourseDetailDTOBuilderAndGetters() {
        SectionDTO section1 = SectionDTO.builder()
                .id(1L)
                .title("Section 1")
                .articles(List.of())
                .build();

        SectionDTO section2 = SectionDTO.builder()
                .id(2L)
                .title("Section 2")
                .articles(List.of())
                .build();

        CourseDetailDTO courseDetail = CourseDetailDTO.builder()
                .id(1L)
                .title("Java Programming")
                .category("Programming")
                .instructor("John Doe")
                .price(new BigDecimal("100.00"))
                .is_free(false)
                .description("Learn Java from scratch")
                .created_at("2023-01-01")
                .updated_at("2023-01-10")
                .sections(List.of(section1, section2))
                .build();

        assertEquals(1L, courseDetail.getId());
        assertEquals("Java Programming", courseDetail.getTitle());
        assertEquals("Programming", courseDetail.getCategory());
        assertEquals("John Doe", courseDetail.getInstructor());
        assertEquals(new BigDecimal("100.00"), courseDetail.getPrice());
        assertEquals(false, courseDetail.is_free());
        assertEquals("Learn Java from scratch", courseDetail.getDescription());
        assertEquals("2023-01-01", courseDetail.getCreated_at());
        assertEquals("2023-01-10", courseDetail.getUpdated_at());
        assertEquals(2, courseDetail.getSections().size());
        assertEquals("Section 1", courseDetail.getSections().get(0).getTitle());
        assertEquals("Section 2", courseDetail.getSections().get(1).getTitle());
    }
    
}
