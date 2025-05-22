package id.ac.ui.cs.advprog.udehnihcourse.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentStudentDTO {
    private String studentId;
    private String studentName;
    private LocalDateTime enrolledAt;
}