package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO to serve Course data to clients.
 * Design Pattern: DTO
 */
@AllArgsConstructor
@Builder
@Data
public class CourseDetailDTO {
    private Long id;
    private String title;
    private String category;
    private String instructor;
    private BigDecimal price;
    private boolean is_free;
    private String description;
    private String created_at;
    private String updated_at;
    private List<SectionDTO> sections;
}
