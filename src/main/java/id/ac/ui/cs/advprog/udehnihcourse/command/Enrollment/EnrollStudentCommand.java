package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrollmentDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EnrollStudentCommand implements EnrollmentCommand<EnrollmentDTO> {
    private final CourseEnrollmentService enrollmentService;
    private final Long studentId;
    private final Long courseId;
    private final String paymentMethod;

    @Override
    public EnrollmentDTO execute() {
        return enrollmentService.enrollStudentInCourse(studentId, courseId, paymentMethod);
    }
}
