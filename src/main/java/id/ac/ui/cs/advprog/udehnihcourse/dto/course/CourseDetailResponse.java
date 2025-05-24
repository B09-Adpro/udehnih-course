package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Course detail response (used in GET /api/courses/{id}).
 * Design Pattern: DTO, Static Factory Method
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String tutorId;
    private BigDecimal price;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int enrollmentCount;
    private int sectionCount;
    private int articleCount;

    public static CourseDetailResponse fromEntity(Course course) {
        int sectionsSize = (course.getSections() != null) ? course.getSections().size() : 0;
        int articlesSize = (course.getSections() != null) ?
                course.getSections().stream()
                        .mapToInt(s -> (s.getArticles() != null) ? s.getArticles().size() : 0)
                        .sum()
                : 0;

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .tutorId(course.getTutorId())
                .price(course.getPrice())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .enrollmentCount(course.getEnrollmentCount())
                .sectionCount(sectionsSize)
                .articleCount(articlesSize)
                .build();
    }
}