package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrolledCourseDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class GetEnrollmentsCommand implements EnrollmentCommand<List<EnrolledCourseDTO>> {
    private final CourseEnrollmentService enrollmentService;
    private final Long studentId;

    @Override
    public List<EnrolledCourseDTO> execute() {
        return enrollmentService.getStudentEnrollments(studentId);
    }
}

