package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for creating a new course. Maps to POST /api/courses body.
 * Design Pattern: DTO
 */
@Data
public class CourseCreateRequest {
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
}
