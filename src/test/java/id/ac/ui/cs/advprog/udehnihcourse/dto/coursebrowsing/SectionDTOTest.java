package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.List;

public class SectionDTOTest {
    @Test
    void testSectionDTOBuilderAndGetters() {
        // Create mock ArticleDTO objects
        ArticleDTO article1 = ArticleDTO.builder()
                .id(1L)
                .title("Article 1")
                .content("Content 1")
                .contentType("text")
                .build();

        ArticleDTO article2 = ArticleDTO.builder()
                .id(2L)
                .title("Article 2")
                .content("Content 2")
                .contentType("text")
                .build();

        // Create a SectionDTO object using the builder
        SectionDTO sectionDTO = SectionDTO.builder()
                .id(1L)
                .title("Section 1")
                .articles(List.of(article1, article2))
                .build();

        // Assert the values using getters
        assertEquals(1L, sectionDTO.getId());
        assertEquals("Section 1", sectionDTO.getTitle());
        assertEquals(2, sectionDTO.getArticles().size());
        assertEquals("Article 1", sectionDTO.getArticles().get(0).getTitle());
        assertEquals("Article 2", sectionDTO.getArticles().get(1).getTitle());
    }
}
