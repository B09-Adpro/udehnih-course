package id.ac.ui.cs.advprog.udehnihcourse.dto.tutor;

import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the response after submitting a Tutor application. Maps to POST /api/tutors/apply response.
 * Design Pattern: DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorApplicationResponse {
    private String message;
    private Long applicationId;
    private TutorRegistrationStatus status;
}
