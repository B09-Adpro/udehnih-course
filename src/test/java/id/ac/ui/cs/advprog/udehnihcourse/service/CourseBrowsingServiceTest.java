package id.ac.ui.cs.advprog.udehnihcourse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseListDTO;
import id.ac.ui.cs.advprog.udehnihcourse.model.Article;
import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Section;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public class CourseBrowsingServiceTest {
    
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseBrowsingService courseBrowsingService;

    private Course course;
    private Section section;
    private Article article;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        article = new Article();
        article.setId(1L);
        article.setTitle("Article 1");
        article.setContent("Content of Article 1");

        section = new Section();
        section.setId(1L);
        section.setTitle("Section 1");
        section.setArticles(List.of(article));

        course = Course.builder()
                .id(1L)
                .title("Java Programming")
                .description("Learn Java from scratch")
                .tutorId("tutor-1")
                .price(new BigDecimal("100.00"))
                .createdAt(java.time.LocalDateTime.of(2023, 1, 1, 10, 0))
                .updatedAt(java.time.LocalDateTime.of(2023, 1, 1, 10, 0)) // Add updatedAt
                .sections(List.of(section))
                .build();
    }

    @Test
    void testGetAllCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseListDTO> courses = courseBrowsingService.getAllCourses();

        assertEquals(1, courses.size());
        assertEquals("Java Programming", courses.get(0).getTitle());
        assertEquals(new BigDecimal("100.00"), courses.get(0).getPrice());
    }

    @Test
    void testSearchCourses() {
        when(courseRepository.findByTitleContainingIgnoreCase("java")).thenReturn(List.of(course));

        List<CourseListDTO> courses = courseBrowsingService.searchCourses("java");

        assertEquals(1, courses.size());
        assertEquals("Java Programming", courses.get(0).getTitle());
        assertEquals(new BigDecimal("100.00"), courses.get(0).getPrice());
    }

    @Test
    void testGetCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDetailDTO courseDetail = courseBrowsingService.getCourseById(1L);

        assertEquals(1L, courseDetail.getId());
        assertEquals("Java Programming", courseDetail.getTitle());
        assertEquals("Learn Java from scratch", courseDetail.getDescription());
        assertEquals(new BigDecimal("100.00"), courseDetail.getPrice());
        assertEquals(1, courseDetail.getSections().size());
        assertEquals("Section 1", courseDetail.getSections().get(0).getTitle());
    }
}
