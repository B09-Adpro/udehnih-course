package id.ac.ui.cs.advprog.udehnihcourse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    private Section section;
    private Course course;
    private Article article;

    @BeforeEach
    void setUp() {
        this.course = Course.builder().id(1L).title("Test Course").build();
        this.section = new Section();
        this.section.setId(10L);
        this.section.setTitle("Section Title");
        this.section.setCourse(this.course);

        this.article = new Article();
        this.article.setId(100L);
        this.article.setTitle("Article Title");
        this.article.setContent("Article Content");
    }

    @Test
    void testSectionCreationAndGetters() {
        assertNotNull(section);
        assertEquals(10L, section.getId());
        assertEquals("Section Title", section.getTitle());
        assertEquals(course, section.getCourse());
        assertNotNull(section.getArticles());
        assertTrue(section.getArticles().isEmpty());
    }

    @Test
    void testSectionSetters() {
        Section newSection = new Section();
        Course otherCourse = Course.builder().id(2L).build();

        newSection.setId(99L);
        newSection.setTitle("New Section Title");
        newSection.setCourse(otherCourse);
        newSection.setArticles(new ArrayList<>());

        assertEquals(99L, newSection.getId());
        assertEquals("New Section Title", newSection.getTitle());
        assertEquals(otherCourse, newSection.getCourse());
        assertNotNull(newSection.getArticles());
    }

    @Test
    void testAddArticle() {
        section.addArticle(article);

        assertEquals(1, section.getArticles().size());
        assertTrue(section.getArticles().contains(article));

        assertEquals(section, article.getSection());
    }

    @Test
    void testRemoveArticle() {
        section.addArticle(article);
        assertEquals(1, section.getArticles().size());
        assertEquals(section, article.getSection());

        section.removeArticle(article);

        assertTrue(section.getArticles().isEmpty());
        assertNull(article.getSection());
    }

    @Test
    void testToStringDoesNotCauseStackOverflow() {
        section.addArticle(article);
        assertDoesNotThrow(() -> section.toString());
        assertFalse(section.toString().contains("Article{"));
    }

    @Test
    void testEquals_sameObject_shouldReturnTrue() {
        assertEquals(section, section);
    }

    @Test
    void testEquals_nullObject_shouldReturnFalse() {
        assertNotEquals(null, section);
    }

    @Test
    void testEquals_differentClass_shouldReturnFalse() {
        assertNotEquals(section, new String("not a section"));
    }

    @Test
    void testEquals_sameId_shouldReturnTrue() {
        Section section1 = new Section();
        section1.setId(10L);
        section1.setTitle("Section Title");

        Section section2 = new Section();
        section2.setId(10L);
        section2.setTitle("Another Section Title");

        assertEquals(section1, section2);
        assertEquals(section2, section1);
    }

    @Test
    void testEquals_differentId_shouldReturnFalse() {
        Section section1 = new Section();
        section1.setId(10L);

        Section section2 = new Section();
        section2.setId(11L);

        assertNotEquals(section1, section2);
        assertNotEquals(section2, section1);
    }

    @Test
    void testEquals_oneIdNull_shouldReturnFalse() {
        Section section1 = new Section();
        section1.setTitle("Transient Section");

        Section section2 = new Section();
        section2.setId(10L);

        assertNotEquals(section1, section2);
        assertNotEquals(section2, section1);
    }

    @Test
    void testEquals_bothIdNull_shouldReturnFalseUnlessSameRef() {
        Section section1 = new Section();
        Section section2 = new Section();

        assertNotEquals(section1, section2);
        assertEquals(section1, section1);
    }

    @Test
    void testHashCode_consistency() {
        int initialHashCode = section.hashCode();
        assertEquals(initialHashCode, section.hashCode());
        assertEquals(initialHashCode, section.hashCode());
    }

    @Test
    void testHashCode_basedOnClass() {
        Section section1 = new Section();
        section1.setId(1L);
        Section section2 = new Section();
        section2.setId(2L);
        Section section3 = new Section();

        assertEquals(section1.hashCode(), section2.hashCode());
        assertEquals(section1.hashCode(), section3.hashCode());

        Section section4 = new Section();
        section4.setId(1L);
        assertEquals(section1, section4);
        assertEquals(section1.hashCode(), section4.hashCode());
    }
}
