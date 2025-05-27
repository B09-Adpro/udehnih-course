package id.ac.ui.cs.advprog.udehnihcourse.dto.staff;

import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class StaffCoursePendingReviewViewDTOTest {

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

        section = new Section();
        section.setId(1L);
        section.setTitle("Test Section");
        section.setArticles(Arrays.asList(article));

        course = Course.builder()
                .id(1L)
                .title("Test Course")
                .category("Programming")
                .price(new BigDecimal("100.00"))
                .tutorId("tutor-123")
                .status(CourseStatus.PENDING_REVIEW)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .sections(Arrays.asList(section))
                .build();
    }

    @Test
    void fromEntity_shouldMapAllFieldsAndCalculateCounts() {
        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertNotNull(dto);
        assertEquals(course.getId(), dto.getCourseId());
        assertEquals(course.getTitle(), dto.getTitle());
        assertEquals(course.getCategory(), dto.getCategory());
        assertEquals(course.getPrice(), dto.getPrice());
        assertEquals(course.getTutorId(), dto.getTutorId());
        assertEquals(course.getCreatedAt(), dto.getCreatedAt());
        assertEquals(course.getUpdatedAt(), dto.getUpdatedAt());
        assertEquals(course.getStatus(), dto.getStatus());
        assertEquals(1, dto.getSectionCount());
        assertEquals(1, dto.getArticleCount());
    }

    @Test
    void fromEntity_whenSectionsIsNull_shouldHandleGracefully() {
        course.setSections(null);

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertNotNull(dto);
        assertEquals(course.getId(), dto.getCourseId());
        assertEquals(0, dto.getSectionCount());
        assertEquals(0, dto.getArticleCount());
    }

    @Test
    void fromEntity_whenSectionsIsEmpty_shouldReturnZeroCounts() {
        course.setSections(Collections.emptyList());

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertNotNull(dto);
        assertEquals(0, dto.getSectionCount());
        assertEquals(0, dto.getArticleCount());
    }

    @Test
    void fromEntity_whenSectionHasNullArticles_shouldHandleGracefully() {
        section.setArticles(null);

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertNotNull(dto);
        assertEquals(1, dto.getSectionCount());
        assertEquals(0, dto.getArticleCount());
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

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertEquals(3, dto.getSectionCount());
        assertEquals(3, dto.getArticleCount()); // 2 + 1 + 0 articles
    }

    @Test
    void fromEntity_withMultipleSectionsAndArticles_shouldCalculateCorrectly() {
        // Create additional articles
        Article article2 = new Article();
        article2.setId(2L);

        Article article3 = new Article();
        article3.setId(3L);

        Article article4 = new Article();
        article4.setId(4L);

        // Create additional section
        Section section2 = new Section();
        section2.setId(2L);
        section2.setTitle("Section 2");
        section2.setArticles(Arrays.asList(article2, article3, article4));

        // Update first section to have 2 articles
        section.setArticles(Arrays.asList(article, article2));

        // Update course with 2 sections
        course.setSections(Arrays.asList(section, section2));

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertEquals(2, dto.getSectionCount());
        assertEquals(5, dto.getArticleCount()); // 2 + 3 articles
    }

    @Test
    void fromEntity_withDifferentCourseStatuses_shouldMapCorrectly() {
        // Test PENDING_REVIEW status
        course.setStatus(CourseStatus.PENDING_REVIEW);
        StaffCoursePendingReviewViewDTO pendingDto = StaffCoursePendingReviewViewDTO.fromEntity(course);
        assertEquals(CourseStatus.PENDING_REVIEW, pendingDto.getStatus());

        // Test REJECTED status
        course.setStatus(CourseStatus.REJECTED);
        StaffCoursePendingReviewViewDTO rejectedDto = StaffCoursePendingReviewViewDTO.fromEntity(course);
        assertEquals(CourseStatus.REJECTED, rejectedDto.getStatus());

        // Test PUBLISHED status
        course.setStatus(CourseStatus.PUBLISHED);
        StaffCoursePendingReviewViewDTO publishedDto = StaffCoursePendingReviewViewDTO.fromEntity(course);
        assertEquals(CourseStatus.PUBLISHED, publishedDto.getStatus());

        // Test DRAFT status
        course.setStatus(CourseStatus.DRAFT);
        StaffCoursePendingReviewViewDTO draftDto = StaffCoursePendingReviewViewDTO.fromEntity(course);
        assertEquals(CourseStatus.DRAFT, draftDto.getStatus());
    }

    @Test
    void fromEntity_withNullCourse_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            StaffCoursePendingReviewViewDTO.fromEntity(null);
        });
    }

    @Test
    void builder_shouldWorkCorrectly() {
        LocalDateTime testCreatedAt = LocalDateTime.now().minusDays(10);
        LocalDateTime testUpdatedAt = LocalDateTime.now().minusDays(2);

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.builder()
                .courseId(2L)
                .title("Builder Course")
                .category("Builder Category")
                .price(new BigDecimal("199.99"))
                .tutorId("builder-tutor")
                .createdAt(testCreatedAt)
                .updatedAt(testUpdatedAt)
                .status(CourseStatus.PENDING_REVIEW)
                .sectionCount(5)
                .articleCount(25)
                .build();

        assertEquals(2L, dto.getCourseId());
        assertEquals("Builder Course", dto.getTitle());
        assertEquals("Builder Category", dto.getCategory());
        assertEquals(new BigDecimal("199.99"), dto.getPrice());
        assertEquals("builder-tutor", dto.getTutorId());
        assertEquals(testCreatedAt, dto.getCreatedAt());
        assertEquals(testUpdatedAt, dto.getUpdatedAt());
        assertEquals(CourseStatus.PENDING_REVIEW, dto.getStatus());
        assertEquals(5, dto.getSectionCount());
        assertEquals(25, dto.getArticleCount());
    }

    @Test
    void noArgsConstructor_shouldWork() {
        StaffCoursePendingReviewViewDTO dto = new StaffCoursePendingReviewViewDTO();

        assertNotNull(dto);
        assertNull(dto.getCourseId());
        assertNull(dto.getTitle());
        assertNull(dto.getCategory());
        assertNull(dto.getPrice());
        assertNull(dto.getTutorId());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getStatus());
        assertEquals(0, dto.getSectionCount());
        assertEquals(0, dto.getArticleCount());
    }

    @Test
    void allArgsConstructor_shouldWork() {
        LocalDateTime testCreatedAt = LocalDateTime.now().minusDays(15);
        LocalDateTime testUpdatedAt = LocalDateTime.now().minusDays(3);

        StaffCoursePendingReviewViewDTO dto = new StaffCoursePendingReviewViewDTO(
                3L,
                "Constructor Course",
                "Constructor Category",
                new BigDecimal("299.99"),
                "constructor-tutor",
                testCreatedAt,
                testUpdatedAt,
                CourseStatus.REJECTED,
                7,
                35
        );

        assertEquals(3L, dto.getCourseId());
        assertEquals("Constructor Course", dto.getTitle());
        assertEquals("Constructor Category", dto.getCategory());
        assertEquals(new BigDecimal("299.99"), dto.getPrice());
        assertEquals("constructor-tutor", dto.getTutorId());
        assertEquals(testCreatedAt, dto.getCreatedAt());
        assertEquals(testUpdatedAt, dto.getUpdatedAt());
        assertEquals(CourseStatus.REJECTED, dto.getStatus());
        assertEquals(7, dto.getSectionCount());
        assertEquals(35, dto.getArticleCount());
    }

    @Test
    void settersAndGetters_shouldWork() {
        StaffCoursePendingReviewViewDTO dto = new StaffCoursePendingReviewViewDTO();
        LocalDateTime testCreatedAt = LocalDateTime.now().minusDays(20);
        LocalDateTime testUpdatedAt = LocalDateTime.now().minusDays(4);

        dto.setCourseId(4L);
        dto.setTitle("Setter Course");
        dto.setCategory("Setter Category");
        dto.setPrice(new BigDecimal("399.99"));
        dto.setTutorId("setter-tutor");
        dto.setCreatedAt(testCreatedAt);
        dto.setUpdatedAt(testUpdatedAt);
        dto.setStatus(CourseStatus.PUBLISHED);
        dto.setSectionCount(10);
        dto.setArticleCount(50);

        assertEquals(4L, dto.getCourseId());
        assertEquals("Setter Course", dto.getTitle());
        assertEquals("Setter Category", dto.getCategory());
        assertEquals(new BigDecimal("399.99"), dto.getPrice());
        assertEquals("setter-tutor", dto.getTutorId());
        assertEquals(testCreatedAt, dto.getCreatedAt());
        assertEquals(testUpdatedAt, dto.getUpdatedAt());
        assertEquals(CourseStatus.PUBLISHED, dto.getStatus());
        assertEquals(10, dto.getSectionCount());
        assertEquals(50, dto.getArticleCount());
    }

    @Test
    void fromEntity_withZeroPrice_shouldHandleCorrectly() {
        course.setPrice(BigDecimal.ZERO);

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertEquals(BigDecimal.ZERO, dto.getPrice());
    }

    @Test
    void fromEntity_withLargeNumbers_shouldHandleCorrectly() {
        // Create a course with many sections and articles
        course.setSections(Collections.nCopies(100, section)); // 100 sections
        section.setArticles(Collections.nCopies(50, article)); // 50 articles per section

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertEquals(100, dto.getSectionCount());
        assertEquals(5000, dto.getArticleCount()); // 100 * 50
    }

    @Test
    void fromEntity_withNullTimestamps_shouldHandleGracefully() {
        course.setCreatedAt(null);
        course.setUpdatedAt(null);

        StaffCoursePendingReviewViewDTO dto = StaffCoursePendingReviewViewDTO.fromEntity(course);

        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertEquals(course.getId(), dto.getCourseId());
    }
}