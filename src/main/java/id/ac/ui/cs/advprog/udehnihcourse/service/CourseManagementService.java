package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.UserInfoResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.*;

import id.ac.ui.cs.advprog.udehnihcourse.dto.staff.StaffCoursePendingReviewViewDTO;
import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import id.ac.ui.cs.advprog.udehnihcourse.repository.EnrollmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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
@Slf4j
public class CourseManagementService {

    private final CourseRepository courseRepository;
    private final TutorRegistrationRepository tutorRegistrationRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthServiceClient authServiceClient;


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
                .status(CourseStatus.DRAFT)
                .build();

        Course savedCourse = courseRepository.save(course);

        return CourseResponse.builder()
                .message("Course created successfully")
                .courseId(savedCourse.getId())
                .status(savedCourse.getStatus())
                .build();
    }

    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found with ID: " + courseId));

        verifyCourseOwnership(course, tutorId);
        verifyCourseIsModifiable(course);

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

        verifyCourseOwnership(course, tutorId);

        courseRepository.delete(course);
    }

    @Transactional(readOnly = true)
    public List<TutorCourseListItem> getCoursesByTutor(String tutorId) {
        verifyUserIsAcceptedTutor(tutorId);

        List<Course> courses = courseRepository.findByTutorId(tutorId);
        return courses.stream()
                .map(course -> {
                    log.debug("DEBUG: Processing Course ID: " + course.getId());

                    long enrollmentCount = enrollmentRepository.countByCourseId(course.getId());

                    CourseStatus currentStatus = course.getStatus();
                    log.debug("DEBUG: Course ID: " + course.getId() + ", Status from Entity: " + currentStatus);

                    return TutorCourseListItem.builder()
                            .id(course.getId())
                            .title(course.getTitle())
                            .category(course.getCategory())
                            .price(course.getPrice())
                            .enrollmentCount((int) enrollmentCount)
                            .createdAt(course.getCreatedAt())
                            .status(currentStatus)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseEnrollmentStudentDTO> getEnrolledStudentsForCourse(Long courseId, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId));
        verifyCourseOwnership(course, tutorId);

        List<Enrollment> enrolledAndPaidEnrollments = enrollmentRepository.findByCourseId(courseId).stream()
                .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.ENROLLED)
                .collect(Collectors.toList());

        if (enrolledAndPaidEnrollments.isEmpty()) {
            log.info("No enrollments with status ENROLLED found for course ID: {}", courseId);
            return Collections.emptyList();
        }

        List<CourseEnrollmentStudentDTO> enrolledStudents = enrolledAndPaidEnrollments.stream()
                .map(enrollment -> {
                    String studentName = "Student " + enrollment.getStudentId();

                    UserInfoResponse userInfo = authServiceClient.getUserInfoById(String.valueOf(enrollment.getStudentId()));

                    if (userInfo != null && userInfo.getName() != null && !userInfo.getName().isEmpty()) {
                        studentName = userInfo.getName();
                    } else {
                        System.err.println("Could not retrieve name from Auth Service for student ID: " + enrollment.getStudentId() + ". Using default.");
                    }

                    return CourseEnrollmentStudentDTO.builder()
                            .studentId(String.valueOf(enrollment.getStudentId()))
                            .studentName(studentName)
                            .enrolledAt(enrollment.getEnrolledAt())
                            .build();
                })
                .collect(Collectors.toList());

        if (enrolledStudents.isEmpty()) {
            System.out.println("DEBUG: No enrolled students found in database for course " + courseId);
        }

        return enrolledStudents;
    }


    public void submitCourseForReview(Long courseId, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId));
        verifyCourseOwnership(course, tutorId);

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only courses in DRAFT or REJECTED status can be submitted for review. Current status: " + course.getStatus());
        }

        if (course.getSections() == null || course.getSections().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course must have at least one section to be submitted for review.");
        }

        boolean allSectionsHaveArticles = true;
        for (Section section : course.getSections()) {
            if (section.getArticles() == null || section.getArticles().isEmpty()) {
                allSectionsHaveArticles = false;
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section '" + section.getTitle() + "' must have at least one article to be submitted for review.");
            }
        }

        course.setStatus(CourseStatus.PENDING_REVIEW);
        courseRepository.save(course);

        System.out.println("Course " + courseId + " submitted for review by tutor " + tutorId);
    }


    public void verifyCourseOwnership(Course course, String tutorId) {
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Course object is null in ownership check.");
        }
        if (!course.getTutorId().equals(tutorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tutor " + tutorId + " is not authorized for this course.");
        }
    }

    public void verifyCourseIsModifiable(Course course) {
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Course object is null for status check.");
        }
        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Course '" + course.getTitle() + "' cannot be modified while it is PENDING_REVIEW.");
        }
    }

    @Transactional(readOnly = true)
    public List<StaffCoursePendingReviewViewDTO> getCoursesPendingReviewForStaff() {
        log.info("SERVICE: Fetching courses with status PENDING_REVIEW for Staff Dashboard.");
        List<Course> pendingCourses = courseRepository.findByStatus(CourseStatus.PENDING_REVIEW);

        return pendingCourses.stream()
                .map(StaffCoursePendingReviewViewDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Course reviewCourseByStaff(Long courseId, CourseStatus newStatus, String feedback, String staffId) {
        log.info("Staff {} attempting to review course ID {} to status {} with feedback: '{}'",
                staffId, courseId, newStatus, feedback);

        if (newStatus != CourseStatus.PUBLISHED && newStatus != CourseStatus.REJECTED) {
            log.error("Invalid review status by staff: {}. Must be PUBLISHED or REJECTED.", newStatus);
            throw new IllegalArgumentException("Invalid review status from Staff. Must be PUBLISHED or REJECTED.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course not found for ID: {} during review by staff.", courseId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId);
                });

        if (course.getStatus() != CourseStatus.PENDING_REVIEW) {
            log.warn("Staff attempting to review course ID {} which is not PENDING_REVIEW. Current status: {}",
                    courseId, course.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Course can only be reviewed if it's in PENDING_REVIEW status. Current status: " + course.getStatus());
        }

        if (newStatus == CourseStatus.PUBLISHED) {
            if (course.getSections() == null || course.getSections().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course must have at least one section to be published.");
            }
            for (Section section : course.getSections()) {
                if (section.getArticles() == null || section.getArticles().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section '" + section.getTitle() + "' must have at least one article to be published.");
                }
            }
        }

        course.setStatus(newStatus);
        Course savedCourse = courseRepository.save(course);
        log.info("Course ID {} status updated to {} by Staff {}", courseId, newStatus, staffId);

        return savedCourse;
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetailById(Long courseId, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId));

        verifyCourseOwnership(course, tutorId);

        long enrollmentCount = enrollmentRepository.countByCourseId(course.getId());
        course.setEnrollmentCount((int) enrollmentCount);

        return CourseDetailResponse.fromEntity(course);
    }
}
