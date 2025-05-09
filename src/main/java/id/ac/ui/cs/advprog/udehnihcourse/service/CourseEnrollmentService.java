package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.*;
import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import id.ac.ui.cs.advprog.udehnihcourse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseEnrollmentService {

    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentDTO enrollStudentInCourse(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        return EnrollmentDTO.builder()
                .message("Successfully enrolled in course")
                .enrollmentId(enrollment.getId())
                .courseTitle(course.getTitle())
                .status(enrollment.getStatus().name())
                .enrolledAt(enrollment.getEnrolledAt().toString())
                .build();
    }

    public  List<EnrolledCourseDTO> getStudentEnrollments(Long studentId) {
        List<EnrolledCourseDTO> enrolledCourses = enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(enrollment -> EnrolledCourseDTO.builder()
                        .id(enrollment.getCourse().getId())
                        .title(enrollment.getCourse().getTitle())
                        .instructor(getTutorName(enrollment.getCourse().getTutorId()))
                        .build())
                .collect(Collectors.toList());

        return enrolledCourses;
    }

    // TODO : Implement this method to fetch the tutor name based on the tutorId
    private String getTutorName(String tutorId) {
        // Place Holder for actual implementation
        return "Tutor Name";
    }
}