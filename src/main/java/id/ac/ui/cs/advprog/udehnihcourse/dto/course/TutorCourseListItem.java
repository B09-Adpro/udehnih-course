package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for listing courses created by a tutor. Maps to GET /api/tutor/courses response items.
 * Design Pattern: DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorCourseListItem {
    private Long id;
    private String title;
    private String category;
    private BigDecimal price;
    private int enrollmentCount;
    private LocalDateTime createdAt;
}
