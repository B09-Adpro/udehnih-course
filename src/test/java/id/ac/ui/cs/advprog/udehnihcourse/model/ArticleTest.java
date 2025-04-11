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

    @Test
    void testEquals_sameObject_shouldReturnTrue() {
        assertEquals(article, article);
    }

    @Test
    void testEquals_nullObject_shouldReturnFalse() {
        assertNotEquals(null, article);
    }

    @Test
    void testEquals_differentClass_shouldReturnFalse() {
        assertNotEquals(article, new String("not an article"));
    }

    @Test
    void testEquals_sameId_shouldReturnTrue() {
        Article article1 = new Article();
        article1.setId(100L);
        article1.setTitle("Article Title");

        Article article2 = new Article();
        article2.setId(100L);
        article2.setTitle("Another Title");

        assertEquals(article1, article2);
        assertEquals(article2, article1);
    }

    @Test
    void testEquals_differentId_shouldReturnFalse() {
        Article article1 = new Article();
        article1.setId(100L);

        Article article2 = new Article();
        article2.setId(101L);

        assertNotEquals(article1, article2);
        assertNotEquals(article2, article1);
    }

    @Test
    void testEquals_oneIdNull_shouldReturnFalse() {
        Article article1 = new Article();
        article1.setTitle("Transient Article");

        Article article2 = new Article();
        article2.setId(100L);

        assertNotEquals(article1, article2);
        assertNotEquals(article2, article1);
    }

    @Test
    void testEquals_bothIdNull_shouldReturnFalseUnlessSameRef() {
        Article article1 = new Article();
        Article article2 = new Article();

        assertNotEquals(article1, article2);
        assertEquals(article1, article1);
    }

    @Test
    void testHashCode_consistency() {
        int initialHashCode = article.hashCode();
        assertEquals(initialHashCode, article.hashCode());
        assertEquals(initialHashCode, article.hashCode());
    }

    @Test
    void testHashCode_basedOnClass() {
        Article article1 = new Article();
        article1.setId(1L);
        Article article2 = new Article();
        article2.setId(2L); // ID beda
        Article article3 = new Article();

        assertEquals(article1.hashCode(), article2.hashCode());
        assertEquals(article1.hashCode(), article3.hashCode());

        Article article4 = new Article();
        article4.setId(1L);
        assertEquals(article1, article4);
        assertEquals(article1.hashCode(), article4.hashCode());
    }
}
