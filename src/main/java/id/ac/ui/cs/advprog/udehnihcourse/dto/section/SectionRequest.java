package id.ac.ui.cs.advprog.udehnihcourse.dto.section;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating or updating a Section.
 * Design Pattern: DTO
 */
@Data
public class SectionRequest {
    @NotBlank(message = "Section title is mandatory")
    @Size(max = 255, message = "Section title cannot exceed 255 characters")
    private String title;
}