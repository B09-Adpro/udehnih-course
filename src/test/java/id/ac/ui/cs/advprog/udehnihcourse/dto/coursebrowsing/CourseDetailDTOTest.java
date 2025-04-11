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
                .description("Learn Java from scratch")
                .tutorName("John Doe")
                .price(new BigDecimal("100.00"))
                .sections(List.of(section1, section2))
                .build();

        assertEquals(1L, courseDetail.getId());
        assertEquals("Java Programming", courseDetail.getTitle());
        assertEquals("Learn Java from scratch", courseDetail.getDescription());
        assertEquals("John Doe", courseDetail.getTutorName());
        assertEquals(new BigDecimal("100.00"), courseDetail.getPrice());
        assertEquals(2, courseDetail.getSections().size());
        assertEquals("Section 1", courseDetail.getSections().get(0).getTitle());
        assertEquals("Section 2", courseDetail.getSections().get(1).getTitle());
    }
    
}
