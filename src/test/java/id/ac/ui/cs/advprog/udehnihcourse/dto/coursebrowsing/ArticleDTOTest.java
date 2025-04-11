package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ArticleDTOTest {
    
    @Test
    void testArticleDTOBuilderAndGetters() {
        ArticleDTO article = ArticleDTO.builder()
                .id(1L)
                .title("Introduction to Java")
                .content("This is the content of the article.")
                .contentType("text")
                .build();

        assertEquals(1L, article.getId());
        assertEquals("Introduction to Java", article.getTitle());
        assertEquals("This is the content of the article.", article.getContent());
        assertEquals("text", article.getContentType());
    }
    
}
