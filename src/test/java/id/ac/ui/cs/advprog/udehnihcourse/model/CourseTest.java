package id.ac.ui.cs.advprog.udehnihcourse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {
    private Course course;
    private Section section;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        this.course = Course.builder()
                .title("Advanced Programming Course")
                .description("A deep dive into programming concepts.")
                .category("Computer Science")
                .tutorId("tutor-123")
                .price(new BigDecimal("150000.00"))
                .status(CourseStatus.DRAFT)
                .build();

        this.course.setCreatedAt(now);
        this.course.setUpdatedAt(now);

        this.section = new Section();
        this.section.setId(1L);
        this.section.setTitle("Introduction");
    }

    @Test
    void testCourseCreationAndGetters() {
        assertNotNull(course);
        assertEquals("Advanced Programming Course", course.getTitle());
        assertEquals("A deep dive into programming concepts.", course.getDescription());
        assertEquals("Computer Science", course.getCategory());
        assertEquals("tutor-123", course.getTutorId());
        assertEquals(0, new BigDecimal("150000.00").compareTo(course.getPrice()));
        assertEquals(CourseStatus.DRAFT, course.getStatus());
        assertNotNull(course.getSections());
        assertTrue(course.getSections().isEmpty());
        assertNotNull(course.getCreatedAt());
        assertNotNull(course.getUpdatedAt());
        assertEquals(0, course.getEnrollmentCount());
    }

    @Test
    void testCourseSetters() {
        Course newCourse = new Course();
        LocalDateTime now = LocalDateTime.now();

        newCourse.setId(99L);
        newCourse.setTitle("New Title");
        newCourse.setDescription("New Desc");
        newCourse.setCategory("Software Engineering");
        newCourse.setTutorId("tutor-456");
        newCourse.setPrice(BigDecimal.TEN);
        newCourse.setStatus(CourseStatus.PUBLISHED);
        newCourse.setCreatedAt(now);
        newCourse.setUpdatedAt(now);
        newCourse.setSections(new ArrayList<>());
        newCourse.setEnrollmentCount(25);

        assertEquals(99L, newCourse.getId());
        assertEquals("New Title", newCourse.getTitle());
        assertEquals("New Desc", newCourse.getDescription());
        assertEquals("Software Engineering", newCourse.getCategory());
        assertEquals("tutor-456", newCourse.getTutorId());
        assertEquals(0, BigDecimal.TEN.compareTo(newCourse.getPrice()));
        assertEquals(CourseStatus.PUBLISHED, newCourse.getStatus());
        assertEquals(now, newCourse.getCreatedAt());
        assertEquals(now, newCourse.getUpdatedAt());
        assertNotNull(newCourse.getSections());
        assertEquals(25, newCourse.getEnrollmentCount());
    }

    @Test
    void testDefaultValues() {
        Course defaultCourse = Course.builder()
                .title("Default Course")
                .tutorId("tutor-789")
                .build();

        assertEquals(BigDecimal.ZERO, defaultCourse.getPrice());
        assertEquals(CourseStatus.DRAFT, defaultCourse.getStatus());
        assertEquals(0, defaultCourse.getEnrollmentCount());
        assertNotNull(defaultCourse.getSections());
        assertTrue(defaultCourse.getSections().isEmpty());
    }

    @Test
    void testCourseStatusEnum() {
        // Test all possible course statuses
        course.setStatus(CourseStatus.DRAFT);
        assertEquals(CourseStatus.DRAFT, course.getStatus());

        course.setStatus(CourseStatus.PENDING_REVIEW);
        assertEquals(CourseStatus.PENDING_REVIEW, course.getStatus());

        course.setStatus(CourseStatus.REJECTED);
        assertEquals(CourseStatus.REJECTED, course.getStatus());

        course.setStatus(CourseStatus.PUBLISHED);
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());
    }

    @Test
    void testCourseStatusDefault() {
        Course newCourse = new Course();
        assertEquals(CourseStatus.DRAFT, newCourse.getStatus());

        Course builderCourse = Course.builder()
                .title("Test Course")
                .tutorId("tutor-1")
                .build();
        assertEquals(CourseStatus.DRAFT, builderCourse.getStatus());
    }

    @Test
    void testAllCourseStatusesExist() {
        // Verify all enum values exist
        CourseStatus[] allStatuses = CourseStatus.values();
        assertEquals(4, allStatuses.length);

        assertTrue(java.util.Arrays.asList(allStatuses).contains(CourseStatus.DRAFT));
        assertTrue(java.util.Arrays.asList(allStatuses).contains(CourseStatus.PENDING_REVIEW));
        assertTrue(java.util.Arrays.asList(allStatuses).contains(CourseStatus.REJECTED));
        assertTrue(java.util.Arrays.asList(allStatuses).contains(CourseStatus.PUBLISHED));
    }

    @Test
    void testAddSection() {
        course.addSection(section);

        assertEquals(1, course.getSections().size());
        assertTrue(course.getSections().contains(section));
        assertEquals(course, section.getCourse());
    }

    @Test
    void testRemoveSection() {
        course.addSection(section);
        assertEquals(1, course.getSections().size());
        assertEquals(course, section.getCourse());

        course.removeSection(section);

        assertTrue(course.getSections().isEmpty());
        assertNull(section.getCourse());
    }

    @Test
    void testDefaultPriceIsZero() {
        Course freeCourse = Course.builder()
                .title("Free Course")
                .category("General")
                .tutorId("tutor-789")
                .build();

        assertEquals(0, BigDecimal.ZERO.compareTo(freeCourse.getPrice()));
    }

    @Test
    void testDefaultEnrollmentCountIsZero() {
        assertEquals(0, course.getEnrollmentCount());
        course.setEnrollmentCount(10);
        assertEquals(10, course.getEnrollmentCount());
    }

    @Test
    void testTransientEnrollmentCount() {
        // Test that enrollmentCount is transient (not persisted)
        Course newCourse = new Course();
        assertEquals(0, newCourse.getEnrollmentCount());

        newCourse.setEnrollmentCount(50);
        assertEquals(50, newCourse.getEnrollmentCount());
    }

    @Test
    void testCourseWorkflowStatuses() {
        // Test typical course workflow progression
        course.setStatus(CourseStatus.DRAFT);
        assertEquals(CourseStatus.DRAFT, course.getStatus());

        // Course submitted for review
        course.setStatus(CourseStatus.PENDING_REVIEW);
        assertEquals(CourseStatus.PENDING_REVIEW, course.getStatus());

        // Course approved and published
        course.setStatus(CourseStatus.PUBLISHED);
        assertEquals(CourseStatus.PUBLISHED, course.getStatus());
    }

    @Test
    void testCourseRejectionWorkflow() {
        // Test rejection workflow
        course.setStatus(CourseStatus.PENDING_REVIEW);
        assertEquals(CourseStatus.PENDING_REVIEW, course.getStatus());

        // Course rejected, needs revision
        course.setStatus(CourseStatus.REJECTED);
        assertEquals(CourseStatus.REJECTED, course.getStatus());

        // Course resubmitted after fixes
        course.setStatus(CourseStatus.PENDING_REVIEW);
        assertEquals(CourseStatus.PENDING_REVIEW, course.getStatus());
    }

    @Test
    void testBuilderWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Course fullCourse = Course.builder()
                .id(100L)
                .title("Complete Course")
                .description("Full description")
                .category("Technology")
                .tutorId("tutor-full")
                .price(new BigDecimal("299.99"))
                .status(CourseStatus.PUBLISHED)
                .createdAt(now)
                .updatedAt(now)
                .sections(new ArrayList<>())
                .enrollmentCount(100)
                .build();

        assertEquals(100L, fullCourse.getId());
        assertEquals("Complete Course", fullCourse.getTitle());
        assertEquals("Full description", fullCourse.getDescription());
        assertEquals("Technology", fullCourse.getCategory());
        assertEquals("tutor-full", fullCourse.getTutorId());
        assertEquals(0, new BigDecimal("299.99").compareTo(fullCourse.getPrice()));
        assertEquals(CourseStatus.PUBLISHED, fullCourse.getStatus());
        assertEquals(now, fullCourse.getCreatedAt());
        assertEquals(now, fullCourse.getUpdatedAt());
        assertEquals(100, fullCourse.getEnrollmentCount());
        assertNotNull(fullCourse.getSections());
    }

    @Test
    void testCourseStatusTransitions() {
        // Test valid status transitions
        Course transitionCourse = Course.builder()
                .title("Transition Test")
                .tutorId("tutor-transition")
                .build();

        // Start with DRAFT (default)
        assertEquals(CourseStatus.DRAFT, transitionCourse.getStatus());

        // Can go to PENDING_REVIEW
        transitionCourse.setStatus(CourseStatus.PENDING_REVIEW);
        assertEquals(CourseStatus.PENDING_REVIEW, transitionCourse.getStatus());

        // Can be REJECTED
        transitionCourse.setStatus(CourseStatus.REJECTED);
        assertEquals(CourseStatus.REJECTED, transitionCourse.getStatus());

        // Can go back to PENDING_REVIEW after fixes
        transitionCourse.setStatus(CourseStatus.PENDING_REVIEW);
        assertEquals(CourseStatus.PENDING_REVIEW, transitionCourse.getStatus());

        // Can be PUBLISHED
        transitionCourse.setStatus(CourseStatus.PUBLISHED);
        assertEquals(CourseStatus.PUBLISHED, transitionCourse.getStatus());
    }

    @Test
    void testSectionManagement() {
        Section section1 = new Section();
        section1.setTitle("Section 1");

        Section section2 = new Section();
        section2.setTitle("Section 2");

        // Add multiple sections
        course.addSection(section1);
        course.addSection(section2);

        assertEquals(2, course.getSections().size());
        assertTrue(course.getSections().contains(section1));
        assertTrue(course.getSections().contains(section2));
        assertEquals(course, section1.getCourse());
        assertEquals(course, section2.getCourse());

        // Remove one section
        course.removeSection(section1);
        assertEquals(1, course.getSections().size());
        assertFalse(course.getSections().contains(section1));
        assertTrue(course.getSections().contains(section2));
        assertNull(section1.getCourse());
        assertEquals(course, section2.getCourse());
    }
}