package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.ArticleDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseListDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.SectionDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseBrowsingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CourseManagementControllerTest {

    @Mock
    private CourseBrowsingService courseBrowsingService;

    @InjectMocks
    private CourseManagementController courseManagementController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCourses() {
        // Prepare mock data
        CourseListDTO course = CourseListDTO.builder()
                .id(1L)
                .title("Java Course")
                .instructor("John Doe")
                .price(new BigDecimal("100.00"))
                .category("Programming")
                .build();

        when(courseBrowsingService.getAllCourses()).thenReturn(List.of(course));

        // Execute
        ResponseEntity<Map<String, List<CourseListDTO>>> response = courseManagementController.getAllCourses();

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().get("courses").size());
        assertEquals("Java Course", response.getBody().get("courses").get(0).getTitle());
    }

    @Test
    void testSearchCourses() {
        // Prepare mock data
        CourseListDTO course = CourseListDTO.builder()
                .id(1L)
                .title("Java Course")
                .instructor("John Doe")
                .price(new BigDecimal("100.00"))
                .category("Programming")
                .build();

        when(courseBrowsingService.searchCourses("Java")).thenReturn(List.of(course));

        // Execute
        ResponseEntity<Map<String, List<CourseListDTO>>> response = courseManagementController.searchCourses("Java");

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().get("courses").size());
        assertEquals("Java Course", response.getBody().get("courses").get(0).getTitle());
    }

    @Test
    void testGetCourseById() {
        // Prepare mock data
        CourseDetailDTO courseDetail = CourseDetailDTO.builder()
                .id(1L)
                .title("Java Course")
                .instructor("John Doe")
                .price(new BigDecimal("100.00"))
                .description("Learn Java Programming")
                .category("Programming")
                .is_free(false)
                .created_at("2023-01-01T10:00:00")
                .updated_at("2023-01-01T10:00:00")
                .build();

        when(courseBrowsingService.getCourseById(1L, null)).thenReturn(courseDetail);

        // Execute
        ResponseEntity<CourseDetailDTO> response = courseManagementController.getCourseById(1L);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(courseDetail, response.getBody());
    }

    @Test
    void testGetCourseContent() {
        // Prepare mock data
        CourseDetailDTO courseDetail = CourseDetailDTO.builder()
                .id(1L)
                .title("Java Course")
                .instructor("John Doe")
                .price(new BigDecimal("100.00"))
                .description("Learn Java Programming")
                .category("Programming")
                .is_free(false)
                .created_at("2023-01-01T10:00:00")
                .updated_at("2023-01-01T10:00:00")
                .sections(List.of())
                .build();

        when(courseBrowsingService.getCourseById(1L, 12345L)).thenReturn(courseDetail);

        // Execute
        ResponseEntity<CourseDetailDTO> response = courseManagementController
                .getCourseContent("Bearer dummy-token", 1L);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(courseDetail, response.getBody());
        verify(courseBrowsingService).getCourseById(1L, 12345L);
    }

    @Test
    void testGetSectionContent() {
        // Prepare mock data
        SectionDTO sectionDTO = SectionDTO.builder()
                .id(1L)
                .title("Introduction to Java")
                .order(1L)
                .articles(List.of())
                .build();

        when(courseBrowsingService.getSectionById(1L, 2L, 12345L)).thenReturn(sectionDTO);

        // Execute
        ResponseEntity<SectionDTO> response = courseManagementController
                .getSectionContent("Bearer dummy-token", 1L, 2L);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(sectionDTO, response.getBody());
        verify(courseBrowsingService).getSectionById(1L, 2L, 12345L);
    }

    @Test
    void testGetArticleContent() {
        // Prepare mock data
        ArticleDTO articleDTO = ArticleDTO.builder()
                .id(3L)
                .title("Java Variables")
                .content("Java variables are containers for storing data values.")
                .order(1L)
                .build();

        when(courseBrowsingService.getArticleById(1L, 3L, 12345L)).thenReturn(articleDTO);

        // Execute
        ResponseEntity<ArticleDTO> response = courseManagementController
                .getArticleContent("Bearer dummy-token", 1L, 2L, 3L);

        // Verify
        assertEquals(200, response.getStatusCode().value());
        assertEquals(articleDTO, response.getBody());
        verify(courseBrowsingService).getArticleById(1L, 3L, 12345L);
    }
}
