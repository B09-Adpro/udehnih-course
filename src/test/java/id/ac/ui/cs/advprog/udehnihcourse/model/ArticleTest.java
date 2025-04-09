package id.ac.ui.cs.advprog.udehnihcourse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleTest {
    private Article article;
    private Section section;

    @BeforeEach
    void setUp() {
        this.section = new Section();
        this.section.setId(1L);
        this.section.setTitle("Test Section");

        this.article = new Article();
        this.article.setId(100L);
        this.article.setTitle("Article Title");
        this.article.setContent("This is the article content.");
        this.article.setContentType("TEXT");
        this.article.setSection(this.section);
    }

    @Test
    void testArticleCreationAndGetters() {
        assertNotNull(article);
        assertEquals(100L, article.getId());
        assertEquals("Article Title", article.getTitle());
        assertEquals("This is the article content.", article.getContent());
        assertEquals("TEXT", article.getContentType());
        assertEquals(section, article.getSection());
    }

    @Test
    void testArticleSetters() {
        Article newArticle = new Article();
        Section otherSection = new Section();
        otherSection.setId(2L);

        newArticle.setId(999L);
        newArticle.setTitle("New Article Title");
        newArticle.setContent("Different content.");
        newArticle.setContentType("MARKDOWN");
        newArticle.setSection(otherSection);

        assertEquals(999L, newArticle.getId());
        assertEquals("New Article Title", newArticle.getTitle());
        assertEquals("Different content.", newArticle.getContent());
        assertEquals("MARKDOWN", newArticle.getContentType());
        assertEquals(otherSection, newArticle.getSection());
    }

    @Test
    void testDefaultContentTypeIsText() {
        Article defaultArticle = new Article();
        assertEquals("TEXT", defaultArticle.getContentType());
    }

    @Test
    void testToStringDoesNotCauseStackOverflow() {
        assertDoesNotThrow(() -> article.toString());
        assertFalse(article.toString().contains("Section{"));
    }
}
