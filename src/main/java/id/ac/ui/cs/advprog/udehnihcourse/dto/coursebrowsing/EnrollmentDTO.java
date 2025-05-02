package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO to serve Enrollment Response to clients.
 * Design Pattern: DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private String message;
    private Long enrollmentId;
    private String courseTitle;
    private String status;
    private String enrolledAt;
}
