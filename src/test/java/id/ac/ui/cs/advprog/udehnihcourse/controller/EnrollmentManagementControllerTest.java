package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrolledCourseDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrollmentDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.PaymentCallbackDTO;
import id.ac.ui.cs.advprog.udehnihcourse.exception.EnrollmentNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

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
    private PaymentCallbackDTO paymentCallbackDTO;
    private final Long DUMMY_STUDENT_ID = 12345L;
    private final String VALID_API_KEY = "valid-api-key";

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

        paymentCallbackDTO = PaymentCallbackDTO.builder()
                .studentId(DUMMY_STUDENT_ID)
                .courseId(1L)
                .approved(true)
                .build();
    }

    @Test
    void whenEnrollInCourse_thenSuccess() {
        // Arrange
        when(enrollmentService.enrollStudentInCourse(DUMMY_STUDENT_ID, 1L, "credit_card"))
                .thenReturn(enrollmentDTO);

        // Act
        ResponseEntity<EnrollmentDTO> response = controller.enrollInCourse(authToken, 1L, "credit_card");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(enrollmentDTO, response.getBody());
        verify(enrollmentService).enrollStudentInCourse(DUMMY_STUDENT_ID, 1L, "credit_card");
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

    @Test
    void whenHandlePaymentCallbackWithValidApiKey_thenReturnSuccess() {
        // Arrange
        ReflectionTestUtils.setField(controller, "paymentServiceApiKey", VALID_API_KEY);
        doNothing().when(enrollmentService).processPaymentCallback(paymentCallbackDTO);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.handlePaymentCallback(VALID_API_KEY, paymentCallbackDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("received", responseBody.get("status"));
        verify(enrollmentService).processPaymentCallback(paymentCallbackDTO);
    }

    @Test
    void whenHandlePaymentCallbackWithInvalidApiKey_thenReturnUnauthorized() {
        // Act
        ResponseEntity<Map<String, Object>> response = controller.handlePaymentCallback("invalid-key", paymentCallbackDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("error", responseBody.get("status"));
        assertEquals("Unauthorized access", responseBody.get("message"));
        verify(enrollmentService, never()).processPaymentCallback(any(PaymentCallbackDTO.class));
    }

    @Test
    void whenHandlePaymentCallbackAndServiceThrowsException_thenReturnBadRequest() {
        // Arrange
        ReflectionTestUtils.setField(controller, "paymentServiceApiKey", VALID_API_KEY);
        doThrow(new EnrollmentNotFoundException("Pendaftaran tidak ditemukan"))
                .when(enrollmentService).processPaymentCallback(paymentCallbackDTO);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.handlePaymentCallback(VALID_API_KEY, paymentCallbackDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("error", responseBody.get("status"));
        assertEquals("Terjadi kesalahan dalam memproses callback", responseBody.get("message"));

    }
}