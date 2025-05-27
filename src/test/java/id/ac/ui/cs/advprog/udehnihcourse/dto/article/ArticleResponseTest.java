package id.ac.ui.cs.advprog.udehnihcourse.dto.article;

import id.ac.ui.cs.advprog.udehnihcourse.model.Article;
import id.ac.ui.cs.advprog.udehnihcourse.model.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleResponseTest {

    private Article article;
    private Section section;

    @BeforeEach
    void setUp() {
        section = new Section();
        section.setId(1L);
        section.setTitle("Test Section");

        article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test Content");
        article.setContentType("TEXT");
        article.setSection(section);
    }

    @Test
    void fromEntity_shouldMapAllFields() {
        ArticleResponse response = ArticleResponse.fromEntity(article);

        assertNotNull(response);
        assertEquals(article.getId(), response.getId());
        assertEquals(article.getTitle(), response.getTitle());
        assertEquals(article.getContent(), response.getContent());
        assertEquals(article.getContentType(), response.getContentType());
        assertEquals(section.getId(), response.getSectionId());
    }

    @Test
    void fromEntity_whenSectionIsNull_shouldHandleGracefully() {
        article.setSection(null);

        ArticleResponse response = ArticleResponse.fromEntity(article);

        assertNotNull(response);
        assertEquals(article.getId(), response.getId());
        assertEquals(article.getTitle(), response.getTitle());
        assertEquals(article.getContent(), response.getContent());
        assertEquals(article.getContentType(), response.getContentType());
        assertNull(response.getSectionId());
    }

    @Test
    void fromEntity_withNullArticle_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            ArticleResponse.fromEntity(null);
        });
    }

    @Test
    void builder_shouldWorkCorrectly() {
        ArticleResponse response = ArticleResponse.builder()
                .id(1L)
                .title("Builder Test")
                .content("Builder Content")
                .contentType("MARKDOWN")
                .sectionId(10L)
                .build();

        assertEquals(1L, response.getId());
        assertEquals("Builder Test", response.getTitle());
        assertEquals("Builder Content", response.getContent());
        assertEquals("MARKDOWN", response.getContentType());
        assertEquals(10L, response.getSectionId());
    }

    @Test
    void noArgsConstructor_shouldWork() {
        ArticleResponse response = new ArticleResponse();

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getTitle());
        assertNull(response.getContent());
        assertNull(response.getContentType());
        assertNull(response.getSectionId());
    }

    @Test
    void allArgsConstructor_shouldWork() {
        ArticleResponse response = new ArticleResponse(1L, "Title", "Content", "TEXT", 10L);

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Content", response.getContent());
        assertEquals("TEXT", response.getContentType());
        assertEquals(10L, response.getSectionId());
    }

    @Test
    void settersAndGetters_shouldWork() {
        ArticleResponse response = new ArticleResponse();

        response.setId(2L);
        response.setTitle("Setter Title");
        response.setContent("Setter Content");
        response.setContentType("VIDEO");
        response.setSectionId(20L);

        assertEquals(2L, response.getId());
        assertEquals("Setter Title", response.getTitle());
        assertEquals("Setter Content", response.getContent());
        assertEquals("VIDEO", response.getContentType());
        assertEquals(20L, response.getSectionId());
    }
}