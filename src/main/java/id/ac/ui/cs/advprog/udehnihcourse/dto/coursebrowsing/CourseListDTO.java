package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO to serve a list of courses to clients.
 * Design Pattern: DTO
 */
@AllArgsConstructor
@Builder
@Data
public class CourseListDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private String tutorName;
}
