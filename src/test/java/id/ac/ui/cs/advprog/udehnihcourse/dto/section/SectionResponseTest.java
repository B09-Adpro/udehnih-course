package id.ac.ui.cs.advprog.udehnihcourse.dto.section;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SectionResponseTest {

    private Section section;
    private Course course;

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .id(1L)
                .title("Test Course")
                .tutorId("tutor-123")
                .build();

        section = new Section();
        section.setId(1L);
        section.setTitle("Test Section");
        section.setCourse(course);
    }

    @Test
    void fromEntity_shouldMapAllFields() {
        SectionResponse response = SectionResponse.fromEntity(section);

        assertNotNull(response);
        assertEquals(section.getId(), response.getId());
        assertEquals(section.getTitle(), response.getTitle());
        assertEquals(course.getId(), response.getCourseId());
    }

    @Test
    void fromEntity_whenCourseIsNull_shouldHandleGracefully() {
        section.setCourse(null);

        SectionResponse response = SectionResponse.fromEntity(section);

        assertNotNull(response);
        assertEquals(section.getId(), response.getId());
        assertEquals(section.getTitle(), response.getTitle());
        assertNull(response.getCourseId());
    }

    @Test
    void fromEntity_withNullSection_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            SectionResponse.fromEntity(null);
        });
    }

    @Test
    void builder_shouldWorkCorrectly() {
        SectionResponse response = SectionResponse.builder()
                .id(2L)
                .title("Builder Section")
                .courseId(5L)
                .build();

        assertEquals(2L, response.getId());
        assertEquals("Builder Section", response.getTitle());
        assertEquals(5L, response.getCourseId());
    }

    @Test
    void noArgsConstructor_shouldWork() {
        SectionResponse response = new SectionResponse();

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getTitle());
        assertNull(response.getCourseId());
    }

    @Test
    void allArgsConstructor_shouldWork() {
        SectionResponse response = new SectionResponse(1L, "Title", 10L);

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals(10L, response.getCourseId());
    }

    @Test
    void settersAndGetters_shouldWork() {
        SectionResponse response = new SectionResponse();

        response.setId(3L);
        response.setTitle("Setter Title");
        response.setCourseId(30L);

        assertEquals(3L, response.getId());
        assertEquals("Setter Title", response.getTitle());
        assertEquals(30L, response.getCourseId());
    }
}