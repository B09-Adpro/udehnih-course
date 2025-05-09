package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new course. Maps to POST /api/courses body.
 * Design Pattern: DTO
 */
@Data
public class CourseCreateRequest {
    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Category is mandatory")
    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;

    @NotNull(message = "Price is mandatory")
    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;
}
