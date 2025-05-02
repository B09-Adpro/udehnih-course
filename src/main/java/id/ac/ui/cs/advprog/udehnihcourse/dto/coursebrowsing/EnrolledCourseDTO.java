package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to serve EnrolledCourse to clients.
 * Design Pattern: DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledCourseDTO {
    private Long id;
    private String title;
    private String instructor;
}