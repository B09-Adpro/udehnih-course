package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CourseDetailResponseTest {

    private Course course;
    private Section section;
    private Article article;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.now().minusDays(5);
        updatedAt = LocalDateTime.now().minusDays(1);

        article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setContentType("TEXT");

        section = new Section();
        section.setId(1L);
        section.setTitle("Test Section");
        section.setArticles(Arrays.asList(article));
        article.setSection(section);

        course = Course.builder()
                .id(1L)
                .title("Test Course")
                .description("Test Description")
                .category("Programming")
                .tutorId("tutor-123")
                .price(new BigDecimal("100.00"))
                .status(CourseStatus.PUBLISHED)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .sections(Arrays.asList(section))
                .enrollmentCount(10)
                .build();
        section.setCourse(course);
    }

    @Test
    void fromEntity_shouldMapAllFieldsAndCalculateCounts() {
        CourseDetailResponse response = CourseDetailResponse.fromEntity(course);

        assertNotNull(response);
        assertEquals(course.getId(), response.getId());
        assertEquals(course.getTitle(), response.getTitle());
        assertEquals(course.getDescription(), response.getDescription());
        assertEquals(course.getCategory(), response.getCategory());
        assertEquals(course.getTutorId(), response.getTutorId());
        assertEquals(course.getPrice(), response.getPrice());
        assertEquals(course.getStatus(), response.getStatus());
        assertEquals(course.getCreatedAt(), response.getCreatedAt());
        assertEquals(course.getUpdatedAt(), response.getUpdatedAt());
        assertEquals(course.getEnrollmentCount(), response.getEnrollmentCount());
        assertEquals(1, response.getSectionCount());
        assertEquals(1, response.getArticleCount());
    }

    @Test
    void fromEntity_whenSectionsIsNull_shouldHandleGracefully() {
        course.setSections(null);

        CourseDetailResponse response = CourseDetailResponse.fromEntity(course);

        assertNotNull(response);
        assertEquals(course.getId(), response.getId());
        assertEquals(0, response.getSectionCount());
        assertEquals(0, response.getArticleCount());
    }

    @Test
    void fromEntity_whenSectionsIsEmpty_shouldReturnZeroCounts() {
        course.setSections(Collections.emptyList());

        CourseDetailResponse response = CourseDetailResponse.fromEntity(course);

        assertNotNull(response);
        assertEquals(0, response.getSectionCount());
        assertEquals(0, response.getArticleCount());
    }

    @Test
    void fromEntity_whenSectionHasNullArticles_shouldHandleGracefully() {
        section.setArticles(null);

        CourseDetailResponse response = CourseDetailResponse.fromEntity(course);

        assertNotNull(response);
        assertEquals(1, response.getSectionCount());
        assertEquals(0, response.getArticleCount());
    }

    @Test
    void fromEntity_withMultipleSectionsAndArticles_shouldCalculateCorrectly() {
        // Create additional articles
        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Article 2");

        Article article3 = new Article();
        article3.setId(3L);
        article3.setTitle("Article 3");

        // Create additional section
        Section section2 = new Section();
        section2.setId(2L);
        section2.setTitle("Section 2");
        section2.setArticles(Arrays.asList(article2, article3));
        section2.setCourse(course);

        // Update course with 2 sections, total 3 articles
        course.setSections(Arrays.asList(section, section2));

        CourseDetailResponse response = CourseDetailResponse.fromEntity(course);

        assertEquals(2, response.getSectionCount());
        assertEquals(3, response.getArticleCount()); // 1 + 2 articles
    }

    @Test
    void fromEntity_withComplexStructure_shouldCalculateCorrectly() {
        // Setup complex course structure
        Article article1 = new Article();
        Article article2 = new Article();
        Article article3 = new Article();

        Section section1 = new Section();
        section1.setArticles(Arrays.asList(article1, article2));

        Section section2 = new Section();
        section2.setArticles(Arrays.asList(article3));

        Section section3 = new Section();
        section3.setArticles(Collections.emptyList()); // Empty section

        course.setSections(Arrays.asList(section1, section2, section3));

        CourseDetailResponse response = CourseDetailResponse.fromEntity(course);

        assertEquals(3, response.getSectionCount());
        assertEquals(3, response.getArticleCount()); // 2 + 1 + 0 articles
    }

    @Test
    void fromEntity_withNullCourse_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            CourseDetailResponse.fromEntity(null);
        });
    }

    @Test
    void builder_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        CourseDetailResponse response = CourseDetailResponse.builder()
                .id(1L)
                .title("Builder Course")
                .description("Builder Description")
                .category("Builder Category")
                .tutorId("builder-tutor")
                .price(new BigDecimal("99.99"))
                .status(CourseStatus.PUBLISHED)
                .createdAt(now)
                .updatedAt(now)
                .enrollmentCount(50)
                .sectionCount(5)
                .articleCount(25)
                .build();

        assertEquals(1L, response.getId());
        assertEquals("Builder Course", response.getTitle());
        assertEquals("Builder Description", response.getDescription());
        assertEquals("Builder Category", response.getCategory());
        assertEquals("builder-tutor", response.getTutorId());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        assertEquals(CourseStatus.PUBLISHED, response.getStatus());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
        assertEquals(50, response.getEnrollmentCount());
        assertEquals(5, response.getSectionCount());
        assertEquals(25, response.getArticleCount());
    }

    @Test
    void noArgsConstructor_shouldWork() {
        CourseDetailResponse response = new CourseDetailResponse();

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getTitle());
        assertNull(response.getDescription());
        assertEquals(0, response.getEnrollmentCount());
        assertEquals(0, response.getSectionCount());
        assertEquals(0, response.getArticleCount());
    }

    @Test
    void settersAndGetters_shouldWork() {
        CourseDetailResponse response = new CourseDetailResponse();
        LocalDateTime testTime = LocalDateTime.now();

        response.setId(2L);
        response.setTitle("Setter Title");
        response.setDescription("Setter Description");
        response.setCategory("Setter Category");
        response.setTutorId("setter-tutor");
        response.setPrice(new BigDecimal("199.99"));
        response.setStatus(CourseStatus.DRAFT);
        response.setCreatedAt(testTime);
        response.setUpdatedAt(testTime);
        response.setEnrollmentCount(100);
        response.setSectionCount(10);
        response.setArticleCount(50);

        assertEquals(2L, response.getId());
        assertEquals("Setter Title", response.getTitle());
        assertEquals("Setter Description", response.getDescription());
        assertEquals("Setter Category", response.getCategory());
        assertEquals("setter-tutor", response.getTutorId());
        assertEquals(new BigDecimal("199.99"), response.getPrice());
        assertEquals(CourseStatus.DRAFT, response.getStatus());
        assertEquals(testTime, response.getCreatedAt());
        assertEquals(testTime, response.getUpdatedAt());
        assertEquals(100, response.getEnrollmentCount());
        assertEquals(10, response.getSectionCount());
        assertEquals(50, response.getArticleCount());
    }
}