package id.ac.ui.cs.advprog.udehnihcourse.dto.staff;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffCoursePendingReviewViewDTO {
    private Long courseId;
    private String title;
    private String category;
    private BigDecimal price;
    private String tutorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CourseStatus status;
    private int sectionCount;
    private int articleCount;

    public static StaffCoursePendingReviewViewDTO fromEntity(Course course) {
        int sectionsSize = (course.getSections() != null) ? course.getSections().size() : 0;
        int articlesSize = (course.getSections() != null) ?
                course.getSections().stream()
                        .mapToInt(s -> (s.getArticles() != null) ? s.getArticles().size() : 0)
                        .sum()
                : 0;

        return StaffCoursePendingReviewViewDTO.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .category(course.getCategory())
                .price(course.getPrice())
                .tutorId(course.getTutorId())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .status(course.getStatus())
                .sectionCount(sectionsSize)
                .articleCount(articlesSize)
                .build();
    }
}