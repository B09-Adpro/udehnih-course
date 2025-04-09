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
        this.course = Course.builder()
                .title("Advanced Programming Course")
                .description("A deep dive into programming concepts.")
                .category("Computer Science")
                .tutorId("tutor-123")
                .price(new BigDecimal("150000.00"))
                .build();

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
        assertEquals(0, new BigDecimal("150000.00").compareTo(course.getPrice())); // BigDecimal compare
        assertNotNull(course.getSections()); // Default list initialized by @Builder.Default
        assertTrue(course.getSections().isEmpty());
        assertNotNull(course.getCreatedAt());
        assertNotNull(course.getUpdatedAt());
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
        newCourse.setCreatedAt(now);
        newCourse.setUpdatedAt(now);
        newCourse.setSections(new ArrayList<>());

        assertEquals(99L, newCourse.getId());
        assertEquals("New Title", newCourse.getTitle());
        assertEquals("New Desc", newCourse.getDescription());
        assertEquals("Software Engineering", newCourse.getCategory());
        assertEquals("tutor-456", newCourse.getTutorId());
        assertEquals(0, BigDecimal.TEN.compareTo(newCourse.getPrice()));
        assertEquals(now, newCourse.getCreatedAt());
        assertEquals(now, newCourse.getUpdatedAt());
        assertNotNull(newCourse.getSections());
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

}
