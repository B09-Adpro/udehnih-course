package id.ac.ui.cs.advprog.udehnihcourse.dto.tutor;

import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for checking Tutor application status. Maps to GET /api/tutors/status response.
 * Design Pattern: DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorApplicationStatusResponse {
    private Long applicationId;
    private TutorRegistrationStatus status;
    private LocalDateTime submittedAt;
    private String experience;
    private String qualifications;
}
