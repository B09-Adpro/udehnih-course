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
    private String description;
    private String tutorName;
    private BigDecimal price;
    private List<SectionDTO> sections;
}
