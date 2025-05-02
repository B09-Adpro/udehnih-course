package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO to serve Article data to clients.
 * Design Pattern: DTO
 */
@AllArgsConstructor
@Builder
@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String content_Type;
    private String content;
    private Long order;
}
