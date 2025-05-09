package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for updating an existing course. Maps to PUT /api/courses/{courseId} body.
 * Design Pattern: DTO
 */
@Data
public class CourseUpdateRequest {
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;
}
