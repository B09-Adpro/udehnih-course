package id.ac.ui.cs.advprog.udehnihcourse.dto.article;

import id.ac.ui.cs.advprog.udehnihcourse.model.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private String contentType;
    private Long sectionId;

    public static ArticleResponse fromEntity(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .contentType(article.getContentType())
                .sectionId(article.getSection() != null ? article.getSection().getId() : null)
                .build();
    }
}