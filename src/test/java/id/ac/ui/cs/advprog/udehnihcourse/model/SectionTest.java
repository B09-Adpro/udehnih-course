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
}
