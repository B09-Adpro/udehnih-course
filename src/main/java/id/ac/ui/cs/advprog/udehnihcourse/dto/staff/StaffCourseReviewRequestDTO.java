package id.ac.ui.cs.advprog.udehnihcourse.dto.staff;

import id.ac.ui.cs.advprog.udehnihcourse.model.CourseStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StaffCourseReviewRequestDTO {
    @NotNull(message = "New status is mandatory")
    private CourseStatus newStatus;

    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
    private String feedback;
}