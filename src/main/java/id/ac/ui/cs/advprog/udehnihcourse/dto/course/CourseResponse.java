package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for simple course operation responses (CRD).
 * Design Pattern: DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private String message;
    private Long courseId;
}
