package id.ac.ui.cs.advprog.udehnihcourse.dto.tutor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for submitting a Tutor application request. Maps to POST /api/tutors/apply body.
 * Design Pattern: DTO
 */
@Data
public class TutorApplicationRequest {
    @NotBlank(message = "Experience is mandatory")
    @Size(max = 2000, message = "Experience cannot exceed 2000 characters")
    private String experience;

    @NotBlank(message = "Qualifications are mandatory")
    @Size(max = 1000, message = "Qualifications cannot exceed 1000 characters")
    private String qualifications;

    @NotBlank(message = "Bio is mandatory")
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;
}
