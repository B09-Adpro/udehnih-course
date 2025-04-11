package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for updating an existing course. Maps to PUT /api/courses/{courseId} body.
 * Design Pattern: DTO
 */
@Data
public class CourseUpdateRequest {
    private String title;
    private String description;
    private BigDecimal price;
}
