package id.ac.ui.cs.advprog.udehnihcourse.service;


import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseCreateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseUpdateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;
import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;
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

    @InjectMocks
    private CourseManagementService courseManagementService;

    private String tutorId;
    private String otherTutorId;
    private Long courseId;
    private Course course;
    private TutorRegistration acceptedTutorReg;

    @BeforeEach
    void setUp() {
        tutorId = "tutor-1";
        otherTutorId = "tutor-2";
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
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
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

        verify(tutorRegistrationRepository, times(1)).findByStudentIdAndStatus(tutorId, TutorRegistrationStatus.ACCEPTED);
        verify(courseRepository, times(1)).save(argThat(c ->
                c.getTitle().equals("New Course") &&
                        c.getDescription().equals("New Desc") &&
                        c.getCategory().equals("New Cat") &&
                        c.getPrice().compareTo(new BigDecimal("50.00")) == 0 &&
                        c.getTutorId().equals(tutorId)
        ));
    }

    @Test
    void createCourse_whenTutorIsNotAccepted_shouldThrowForbidden() {
        mockTutorVerification(tutorId, false);

        CourseCreateRequest request = new CourseCreateRequest(); // Content doesn't matter here

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
        // TODO: Mock enrollment service for actual count

        List<TutorCourseListItem> responseList = courseManagementService.getCoursesByTutor(tutorId);

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        TutorCourseListItem item = responseList.get(0);
        assertEquals(course.getId(), item.getId());
        assertEquals(course.getTitle(), item.getTitle());
        assertEquals(course.getCategory(), item.getCategory());
        assertEquals(0, item.getPrice().compareTo(course.getPrice()));
        assertEquals(0, item.getEnrollmentCount()); // Placeholder value
        assertEquals(course.getCreatedAt(), item.getCreatedAt());

        verify(tutorRegistrationRepository, times(1)).findByStudentIdAndStatus(tutorId, TutorRegistrationStatus.ACCEPTED);
        verify(courseRepository, times(1)).findByTutorId(tutorId);
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
        assertTrue(exception.getReason().contains("Not authorized to update this course"));
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
}
