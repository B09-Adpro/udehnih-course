package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrolledCourseDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrollmentDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentManagementControllerTest {

    @Mock
    private CourseEnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentManagementController controller;

    private EnrollmentDTO enrollmentDTO;
    private List<EnrolledCourseDTO> enrolledCourses;
    private String authToken;
    private final Long DUMMY_STUDENT_ID = 12345L;

    @BeforeEach
    void setUp() {
        enrollmentDTO = EnrollmentDTO.builder()
                .enrollmentId(1L)
                .courseTitle("Java Programming")
                .status("ENROLLED")
                .message("Successfully enrolled in course")
                .enrolledAt(LocalDateTime.now().toString())
                .build();

        EnrolledCourseDTO course1 = EnrolledCourseDTO.builder()
                .id(1L)
                .title("Java Programming")
                .instructor("John Doe")
                .build();

        EnrolledCourseDTO course2 = EnrolledCourseDTO.builder()
                .id(2L)
                .title("Python Programming")
                .instructor("Jane Doe")
                .build();

        enrolledCourses = Arrays.asList(course1, course2);
        authToken = "Bearer dummy-token";
    }

    @Test
    void whenEnrollInCourse_thenSuccess() {
        // Arrange
        when(enrollmentService.enrollStudentInCourse(DUMMY_STUDENT_ID, 1L))
                .thenReturn(enrollmentDTO);

        // Act
        ResponseEntity<EnrollmentDTO> response = controller.enrollInCourse(authToken, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(enrollmentDTO, response.getBody());
        verify(enrollmentService).enrollStudentInCourse(DUMMY_STUDENT_ID, 1L);
    }

    @Test
    void whenGetEnrolledCourses_thenReturnList() {
        // Arrange
        when(enrollmentService.getStudentEnrollments(DUMMY_STUDENT_ID))
                .thenReturn(enrolledCourses);

        // Act
        ResponseEntity<Map<String, List<EnrolledCourseDTO>>> response = controller.getEnrolledCourses(authToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(enrolledCourses, response.getBody().get("courses"));
        assertEquals(2, response.getBody().get("courses").size());
        verify(enrollmentService).getStudentEnrollments(DUMMY_STUDENT_ID);
    }

    @Test
    void whenGetEnrolledCoursesWithEmptyList_thenReturnEmptyResponse() {
        // Arrange
        when(enrollmentService.getStudentEnrollments(DUMMY_STUDENT_ID))
                .thenReturn(List.of());

        // Act
        ResponseEntity<Map<String, List<EnrolledCourseDTO>>> response = controller.getEnrolledCourses(authToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("courses").isEmpty());
        verify(enrollmentService).getStudentEnrollments(DUMMY_STUDENT_ID);
    }

    @Test
    void whenTokenWithoutBearer_thenStillProcess() {
        // Arrange
        String tokenWithoutBearer = "dummy-token";
        when(enrollmentService.getStudentEnrollments(DUMMY_STUDENT_ID))
                .thenReturn(enrolledCourses);

        // Act
        ResponseEntity<Map<String, List<EnrolledCourseDTO>>> response =
                controller.getEnrolledCourses(tokenWithoutBearer);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(enrollmentService).getStudentEnrollments(DUMMY_STUDENT_ID);
    }
}