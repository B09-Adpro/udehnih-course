package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseCreateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseUpdateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.TutorRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class handling Course management by Tutors.
 * Design Pattern: Service Layer
 * Design Pattern: Dependency Injection
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CourseManagementService {

    private final CourseRepository courseRepository;
    private final TutorRegistrationRepository tutorRegistrationRepository;
    // TODO: Inject service/client lain jika perlu (misal Enrollment Service untuk count)

    private void verifyUserIsAcceptedTutor(String tutorId) {
        boolean isAcceptedTutor = tutorRegistrationRepository
                .findByStudentIdAndStatus(tutorId, TutorRegistrationStatus.ACCEPTED)
                .isPresent();
        if (!isAcceptedTutor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an authorized Tutor.");
        }
    }

    public CourseResponse createCourse(CourseCreateRequest request, String tutorId) {
        verifyUserIsAcceptedTutor(tutorId);

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice() == null ? java.math.BigDecimal.ZERO : request.getPrice())
                .tutorId(tutorId)
                .build();

        Course savedCourse = courseRepository.save(course);

        return CourseResponse.builder()
                .message("Course created successfully")
                .courseId(savedCourse.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TutorCourseListItem> getCoursesByTutor(String tutorId) {
        verifyUserIsAcceptedTutor(tutorId);

        List<Course> courses = courseRepository.findByTutorId(tutorId);
        return courses.stream()
                .map(course -> TutorCourseListItem.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .category(course.getCategory())
                        .price(course.getPrice())
                        // TODO: perlu enrollment count sungguhan dari Enrollment service
                        .enrollmentCount(0)
                        .createdAt(course.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found with ID: " + courseId));

        if (!course.getTutorId().equals(tutorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not authorized to update this course");
        }

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }

        courseRepository.save(course);

        return CourseResponse.builder()
                .message("Course updated successfully")
                .courseId(course.getId())
                .build();
    }

    public void deleteCourse(Long courseId, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId));

        if (!course.getTutorId().equals(tutorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this course");
        }

        courseRepository.delete(course);
    }
}
