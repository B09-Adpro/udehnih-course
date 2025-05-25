package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.UserInfoResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.*;
import id.ac.ui.cs.advprog.udehnihcourse.dto.staff.StaffCoursePendingReviewViewDTO;
import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.EnrollmentRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.TutorRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseManagementServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TutorRegistrationRepository tutorRegistrationRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private CourseManagementService courseManagementService;

    private String tutorId;
    private String otherTutorId;
    private String staffId;
    private Long courseId;
    private Course course;
    private Course courseWithSections;
    private Course pendingReviewCourse;
    private Course rejectedCourse;
    private Course publishedCourse;
    private TutorRegistration acceptedTutorReg;
    private Section section;
    private Article article;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        tutorId = "tutor-1";
        otherTutorId = "tutor-2";
        staffId = "staff-1";
        courseId = 1L;

        acceptedTutorReg = new TutorRegistration();
        acceptedTutorReg.setId(99L);
        acceptedTutorReg.setStudentId(tutorId);
        acceptedTutorReg.setStatus(TutorRegistrationStatus.ACCEPTED);

        course = Course.builder()
                .id(courseId)
                .title("Original Title")
                .description("Original Desc")
                .category("Original Cat")
                .price(new BigDecimal("100.00"))
                .tutorId(tutorId)
                .status(CourseStatus.DRAFT)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        // Course with sections and articles for review tests
        article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setContent("Test Content");

        section = new Section();
        section.setId(1L);
        section.setTitle("Test Section");
        section.setArticles(Arrays.asList(article));
        article.setSection(section);

        courseWithSections = Course.builder()
                .id(2L)
                .title("Course With Content")
                .tutorId(tutorId)
                .status(CourseStatus.DRAFT)
                .sections(Arrays.asList(section))
                .build();
        section.setCourse(courseWithSections);

        pendingReviewCourse = Course.builder()
                .id(3L)
                .title("Pending Review Course")
                .tutorId(tutorId)
                .status(CourseStatus.PENDING_REVIEW)
                .sections(Arrays.asList(section))
                .build();

        rejectedCourse = Course.builder()
                .id(4L)
                .title("Rejected Course")
                .tutorId(tutorId)
                .status(CourseStatus.REJECTED)
                .sections(Arrays.asList(section))
                .build();

        publishedCourse = Course.builder()
                .id(5L)
                .title("Published Course")
                .tutorId(tutorId)
                .status(CourseStatus.PUBLISHED)
                .sections(Arrays.asList(section))
                .build();

        enrollment = Enrollment.builder()
                .id(1L)
                .studentId(123L)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .enrolledAt(LocalDateTime.now().minusDays(5))
                .build();
    }

    private void mockTutorVerification(String idToCheck, boolean isAccepted) {
        if (isAccepted) {
            when(tutorRegistrationRepository.findByStudentIdAndStatus(idToCheck, TutorRegistrationStatus.ACCEPTED))
                    .thenReturn(Optional.of(acceptedTutorReg));
        } else {
            when(tutorRegistrationRepository.findByStudentIdAndStatus(idToCheck, TutorRegistrationStatus.ACCEPTED))
                    .thenReturn(Optional.empty());
        }
    }

    // EXISTING TESTS (Updated)
    @Test
    void createCourse_whenTutorIsAccepted_shouldSucceed() {
        mockTutorVerification(tutorId, true);

        CourseCreateRequest request = new CourseCreateRequest();
        request.setTitle("New Course");
        request.setDescription("New Desc");
        request.setCategory("New Cat");
        request.setPrice(new BigDecimal("50.00"));

        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course courseToSave = invocation.getArgument(0);
            courseToSave.setId(2L);
            courseToSave.setCreatedAt(LocalDateTime.now());
            courseToSave.setUpdatedAt(LocalDateTime.now());
            return courseToSave;
        });

        CourseResponse response = courseManagementService.createCourse(request, tutorId);

        assertNotNull(response);
        assertEquals("Course created successfully", response.getMessage());
        assertEquals(2L, response.getCourseId());
        assertEquals(CourseStatus.DRAFT, response.getStatus());

        verify(tutorRegistrationRepository, times(1)).findByStudentIdAndStatus(tutorId, TutorRegistrationStatus.ACCEPTED);
        verify(courseRepository, times(1)).save(argThat(c ->
                c.getTitle().equals("New Course") &&
                        c.getDescription().equals("New Desc") &&
                        c.getCategory().equals("New Cat") &&
                        c.getPrice().compareTo(new BigDecimal("50.00")) == 0 &&
                        c.getTutorId().equals(tutorId) &&
                        c.getStatus() == CourseStatus.DRAFT
        ));
    }

    @Test
    void createCourse_whenTutorIsNotAccepted_shouldThrowForbidden() {
        mockTutorVerification(tutorId, false);

        CourseCreateRequest request = new CourseCreateRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.createCourse(request, tutorId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User is not an authorized Tutor"));

        verify(tutorRegistrationRepository, times(1)).findByStudentIdAndStatus(tutorId, TutorRegistrationStatus.ACCEPTED);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void getCoursesByTutor_whenTutorIsAccepted_shouldReturnCourses() {
        mockTutorVerification(tutorId, true);
        when(courseRepository.findByTutorId(tutorId)).thenReturn(Arrays.asList(course));
        when(enrollmentRepository.countByCourseId(courseId)).thenReturn(5L);

        List<TutorCourseListItem> responseList = courseManagementService.getCoursesByTutor(tutorId);

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        TutorCourseListItem item = responseList.get(0);
        assertEquals(course.getId(), item.getId());
        assertEquals(course.getTitle(), item.getTitle());
        assertEquals(course.getCategory(), item.getCategory());
        assertEquals(0, item.getPrice().compareTo(course.getPrice()));
        assertEquals(5, item.getEnrollmentCount());
        assertEquals(course.getCreatedAt(), item.getCreatedAt());
        assertEquals(CourseStatus.DRAFT, item.getStatus());

        verify(tutorRegistrationRepository, times(1)).findByStudentIdAndStatus(tutorId, TutorRegistrationStatus.ACCEPTED);
        verify(courseRepository, times(1)).findByTutorId(tutorId);
        verify(enrollmentRepository, times(1)).countByCourseId(courseId);
    }

    @Test
    void getCoursesByTutor_whenTutorIsNotAccepted_shouldThrowForbidden() {
        mockTutorVerification(tutorId, false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.getCoursesByTutor(tutorId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(tutorRegistrationRepository, times(1)).findByStudentIdAndStatus(tutorId, TutorRegistrationStatus.ACCEPTED);
        verify(courseRepository, never()).findByTutorId(anyString());
    }

    @Test
    void updateCourse_whenTutorOwnsCourse_shouldSucceed() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Desc");
        request.setPrice(new BigDecimal("200.00"));

        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseResponse response = courseManagementService.updateCourse(courseId, request, tutorId);

        assertNotNull(response);
        assertEquals("Course updated successfully", response.getMessage());
        assertEquals(courseId, response.getCourseId());

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(argThat(c ->
                c.getId().equals(courseId) &&
                        c.getTitle().equals("Updated Title") &&
                        c.getDescription().equals("Updated Desc") &&
                        c.getPrice().compareTo(new BigDecimal("200.00")) == 0 &&
                        c.getTutorId().equals(tutorId)
        ));
    }

    @Test
    void updateCourse_whenCourseNotFound_shouldThrowNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());
        CourseUpdateRequest request = new CourseUpdateRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.updateCourse(courseId, request, tutorId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Course not found"));
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void updateCourse_whenTutorDoesNotOwnCourse_shouldThrowForbidden() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        CourseUpdateRequest request = new CourseUpdateRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.updateCourse(courseId, request, otherTutorId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Tutor " + otherTutorId + " is not authorized for this course"));
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void updateCourse_whenCoursePendingReview_shouldThrowForbidden() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(pendingReviewCourse));
        CourseUpdateRequest request = new CourseUpdateRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.updateCourse(courseId, request, tutorId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("cannot be modified while it is PENDING_REVIEW"));
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteCourse_whenTutorOwnsCourse_shouldSucceed() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseRepository).delete(course);

        assertDoesNotThrow(() -> courseManagementService.deleteCourse(courseId, tutorId));

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void deleteCourse_whenCourseNotFound_shouldThrowNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.deleteCourse(courseId, tutorId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).delete(any(Course.class));
    }

    @Test
    void deleteCourse_whenTutorDoesNotOwnCourse_shouldThrowForbidden() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.deleteCourse(courseId, otherTutorId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).delete(any(Course.class));
    }

    // NEW TESTS FOR ADDITIONAL FUNCTIONALITY

    @Test
    void getCourseDetailById_whenTutorOwnsCourse_shouldReturnDetail() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseWithSections));
        when(enrollmentRepository.countByCourseId(courseId)).thenReturn(10L);

        CourseDetailResponse response = courseManagementService.getCourseDetailById(courseId, tutorId);

        assertNotNull(response);
        assertEquals(courseWithSections.getId(), response.getId());
        assertEquals(courseWithSections.getTitle(), response.getTitle());
        assertEquals(courseWithSections.getDescription(), response.getDescription());
        assertEquals(10, response.getEnrollmentCount());
        assertEquals(1, response.getSectionCount());
        assertEquals(1, response.getArticleCount());
        assertEquals(CourseStatus.DRAFT, response.getStatus());

        verify(courseRepository, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).countByCourseId(courseId);
    }

    @Test
    void getCourseDetailById_whenCourseNotFound_shouldThrowNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.getCourseDetailById(courseId, tutorId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void getCourseDetailById_whenTutorDoesNotOwnCourse_shouldThrowForbidden() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.getCourseDetailById(courseId, otherTutorId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    void getEnrolledStudentsForCourse_whenTutorOwnsCourse_shouldReturnStudents() {
        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id("123")
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByCourseId(courseId)).thenReturn(Arrays.asList(enrollment));
        when(authServiceClient.getUserInfoById("123")).thenReturn(userInfo);

        List<CourseEnrollmentStudentDTO> result = courseManagementService.getEnrolledStudentsForCourse(courseId, tutorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        CourseEnrollmentStudentDTO student = result.get(0);
        assertEquals("123", student.getStudentId());
        assertEquals("John Doe", student.getStudentName());
        assertEquals(enrollment.getEnrolledAt(), student.getEnrolledAt());

        verify(courseRepository, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).findByCourseId(courseId);
        verify(authServiceClient, times(1)).getUserInfoById("123");
    }

    @Test
    void getEnrolledStudentsForCourse_whenNoEnrolledStudents_shouldReturnEmptyList() {
        Enrollment pendingEnrollment = Enrollment.builder()
                .id(2L)
                .studentId(124L)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByCourseId(courseId)).thenReturn(Arrays.asList(pendingEnrollment));

        List<CourseEnrollmentStudentDTO> result = courseManagementService.getEnrolledStudentsForCourse(courseId, tutorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courseRepository, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).findByCourseId(courseId);
        verify(authServiceClient, never()).getUserInfoById(anyString());
    }

    @Test
    void getEnrolledStudentsForCourse_whenAuthServiceFails_shouldUseDefaultName() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByCourseId(courseId)).thenReturn(Arrays.asList(enrollment));
        when(authServiceClient.getUserInfoById("123")).thenReturn(null);

        List<CourseEnrollmentStudentDTO> result = courseManagementService.getEnrolledStudentsForCourse(courseId, tutorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Student 123", result.get(0).getStudentName());

        verify(authServiceClient, times(1)).getUserInfoById("123");
    }

    @Test
    void submitCourseForReview_whenValidDraftCourse_shouldUpdateStatus() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseWithSections));
        when(courseRepository.save(any(Course.class))).thenReturn(courseWithSections);

        assertDoesNotThrow(() -> courseManagementService.submitCourseForReview(courseId, tutorId));

        assertEquals(CourseStatus.PENDING_REVIEW, courseWithSections.getStatus());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(courseWithSections);
    }

    @Test
    void submitCourseForReview_whenValidRejectedCourse_shouldUpdateStatus() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(rejectedCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(rejectedCourse);

        assertDoesNotThrow(() -> courseManagementService.submitCourseForReview(courseId, tutorId));

        assertEquals(CourseStatus.PENDING_REVIEW, rejectedCourse.getStatus());
        verify(courseRepository, times(1)).save(rejectedCourse);
    }

    @Test
    void submitCourseForReview_whenCourseAlreadyPendingReview_shouldThrowBadRequest() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(pendingReviewCourse));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.submitCourseForReview(courseId, tutorId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("must have at least one article"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void getCoursesPendingReviewForStaff_shouldReturnPendingCourses() {
        List<Course> pendingCourses = Arrays.asList(pendingReviewCourse);
        when(courseRepository.findByStatus(CourseStatus.PENDING_REVIEW)).thenReturn(pendingCourses);

        List<StaffCoursePendingReviewViewDTO> result = courseManagementService.getCoursesPendingReviewForStaff();

        assertNotNull(result);
        assertEquals(1, result.size());
        StaffCoursePendingReviewViewDTO dto = result.get(0);
        assertEquals(pendingReviewCourse.getId(), dto.getCourseId());
        assertEquals(pendingReviewCourse.getTitle(), dto.getTitle());
        assertEquals(CourseStatus.PENDING_REVIEW, dto.getStatus());
        assertEquals(1, dto.getSectionCount());
        assertEquals(1, dto.getArticleCount());

        verify(courseRepository, times(1)).findByStatus(CourseStatus.PENDING_REVIEW);
    }

    @Test
    void reviewCourseByStaff_whenApproving_shouldUpdateToPublished() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(pendingReviewCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(pendingReviewCourse);

        Course result = courseManagementService.reviewCourseByStaff(courseId, CourseStatus.PUBLISHED, "Approved", staffId);

        assertNotNull(result);
        assertEquals(CourseStatus.PUBLISHED, pendingReviewCourse.getStatus());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(pendingReviewCourse);
    }

    @Test
    void reviewCourseByStaff_whenRejecting_shouldUpdateToRejected() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(pendingReviewCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(pendingReviewCourse);

        Course result = courseManagementService.reviewCourseByStaff(courseId, CourseStatus.REJECTED, "Needs improvement", staffId);

        assertNotNull(result);
        assertEquals(CourseStatus.REJECTED, pendingReviewCourse.getStatus());
        verify(courseRepository, times(1)).save(pendingReviewCourse);
    }

    @Test
    void reviewCourseByStaff_whenCourseNotFound_shouldThrowNotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.reviewCourseByStaff(courseId, CourseStatus.PUBLISHED, "test", staffId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void reviewCourseByStaff_whenCourseNotPendingReview_shouldThrowBadRequest() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course)); // DRAFT status

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.reviewCourseByStaff(courseId, CourseStatus.PUBLISHED, "test", staffId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("PENDING_REVIEW status"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void reviewCourseByStaff_whenInvalidStatus_shouldThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            courseManagementService.reviewCourseByStaff(courseId, CourseStatus.DRAFT, "test", staffId);
        });

        assertTrue(exception.getMessage().contains("Invalid review status"));
        verify(courseRepository, never()).findById(anyLong());
    }

    @Test
    void reviewCourseByStaff_whenPublishingCourseWithoutSections_shouldThrowBadRequest() {
        Course courseWithoutSections = Course.builder()
                .id(courseId)
                .status(CourseStatus.PENDING_REVIEW)
                .sections(Collections.emptyList())
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseWithoutSections));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.reviewCourseByStaff(courseId, CourseStatus.PUBLISHED, "test", staffId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("must have at least one section"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void verifyCourseOwnership_whenValidOwner_shouldNotThrow() {
        assertDoesNotThrow(() -> courseManagementService.verifyCourseOwnership(course, tutorId));
    }

    @Test
    void verifyCourseOwnership_whenInvalidOwner_shouldThrowForbidden() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.verifyCourseOwnership(course, otherTutorId);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("not authorized for this course"));
    }

    @Test
    void verifyCourseOwnership_whenCourseIsNull_shouldThrowInternalServerError() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.verifyCourseOwnership(null, tutorId);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Course object is null"));
    }

    @Test
    void verifyCourseIsModifiable_whenDraftCourse_shouldNotThrow() {
        assertDoesNotThrow(() -> courseManagementService.verifyCourseIsModifiable(course));
    }

    @Test
    void verifyCourseIsModifiable_whenPendingReviewCourse_shouldThrowForbidden() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.verifyCourseIsModifiable(pendingReviewCourse);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("cannot be modified while it is PENDING_REVIEW"));
    }

    @Test
    void verifyCourseIsModifiable_whenCourseIsNull_shouldThrowInternalServerError() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.verifyCourseIsModifiable(null);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Course object is null"));
    }

    @Test
    void submitCourseForReview_whenCourseHasNoSections_shouldThrowBadRequest() {
        Course emptyCourse = Course.builder()
                .id(courseId)
                .tutorId(tutorId)
                .status(CourseStatus.DRAFT)
                .sections(Collections.emptyList())
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(emptyCourse));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.submitCourseForReview(courseId, tutorId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Course must have at least one section"));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void submitCourseForReview_whenSectionHasNoArticles_shouldThrowBadRequest() {
        Section emptySection = new Section();
        emptySection.setTitle("Empty Section");
        emptySection.setArticles(Collections.emptyList());

        Course courseWithEmptySection = Course.builder()
                .id(courseId)
                .tutorId(tutorId)
                .status(CourseStatus.DRAFT)
                .sections(Arrays.asList(emptySection))
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(courseWithEmptySection));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            courseManagementService.submitCourseForReview(courseId, tutorId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        assertTrue(exception.getReason().contains("Only courses in DRAFT or REJECTED status"));
        verify(courseRepository, never()).save(any(Course.class));
    }
}



