package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO to serve Section data to clients.
 * Design Pattern: DTO
 */
@AllArgsConstructor
@Builder
@Data
public class SectionDTO {
    private Long id;
    private String title;
    private List<ArticleDTO> articles;
}
