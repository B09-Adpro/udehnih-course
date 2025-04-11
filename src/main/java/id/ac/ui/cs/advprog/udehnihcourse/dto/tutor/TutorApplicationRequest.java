package id.ac.ui.cs.advprog.udehnihcourse.dto.tutor;

import lombok.Data;

/**
 * DTO for submitting a Tutor application request. Maps to POST /api/tutors/apply body.
 * Design Pattern: DTO
 */
@Data
public class TutorApplicationRequest {
    private String experience;
    private String qualifications;
    private String bio;
}
