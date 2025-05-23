package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.*;
import id.ac.ui.cs.advprog.udehnihcourse.exception.*;
import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import id.ac.ui.cs.advprog.udehnihcourse.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseEnrollmentServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CourseEnrollmentService courseEnrollmentService;

    private Course course;
    private Enrollment enrollment;
    private final String PAYMENT_METHOD = "credit_card";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(courseEnrollmentService, "paymentServiceUrl", "http://localhost:8080");

        course = Course.builder()
                .id(1L)
                .title("Java Programming")
                .description("Learn Java")
                .price(BigDecimal.valueOf(100000))
                .tutorId("tutor1")
                .build();

        enrollment = Enrollment.builder()
                .id(1L)
                .studentId(101L)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .enrolledAt(LocalDateTime.now())
                .build();
    }

    @Test
    void whenEnrollStudentInNonExistentCourse_thenThrowException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () ->
                courseEnrollmentService.enrollStudentInCourse(101L, 1L, PAYMENT_METHOD));

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void whenEnrollAlreadyEnrolledStudent_thenThrowException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(101L, 1L)).thenReturn(true);

        assertThrows(AlreadyEnrolledException.class, () ->
                courseEnrollmentService.enrollStudentInCourse(101L, 1L, PAYMENT_METHOD));

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void whenPaymentFails_thenThrowException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(101L, 1L)).thenReturn(false);

        PaymentResponseDTO paymentResponse = PaymentResponseDTO.builder()
                .success(false)
                .message("Payment failed")
                .build();

        assertThrows(PaymentInitiationFailedException.class, () ->
                courseEnrollmentService.enrollStudentInCourse(101L, 1L, PAYMENT_METHOD));

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void whenGetStudentEnrollments_thenReturnList() {
        List<Enrollment> enrollments = Arrays.asList(enrollment);
        when(enrollmentRepository.findByStudentId(101L)).thenReturn(enrollments);

        List<EnrolledCourseDTO> results = courseEnrollmentService.getStudentEnrollments(101L);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
        verify(enrollmentRepository).findByStudentId(101L);
    }

    @Test
    void whenProcessPaymentCallback_withApprovedPayment_thenUpdateStatus() {
        PaymentCallbackDTO callback = PaymentCallbackDTO.builder()
                .studentId(101L)
                .courseId(1L)
                .approved(true)
                .build();

        when(enrollmentRepository.findByStudentIdAndCourseId(101L, 1L))
                .thenReturn(Optional.of(enrollment));

        courseEnrollmentService.processPaymentCallback(callback);

        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        verify(enrollmentRepository).findByStudentIdAndCourseId(101L, 1L);
    }

    @Test
    void whenProcessPaymentCallback_withRejectedPayment_thenUpdateStatus() {
        PaymentCallbackDTO callback = PaymentCallbackDTO.builder()
                .studentId(101L)
                .courseId(1L)
                .approved(false)
                .build();

        when(enrollmentRepository.findByStudentIdAndCourseId(101L, 1L))
                .thenReturn(Optional.of(enrollment));

        courseEnrollmentService.processPaymentCallback(callback);

        assertEquals(EnrollmentStatus.PAYMENT_FAILED, enrollment.getStatus());
        verify(enrollmentRepository).findByStudentIdAndCourseId(101L, 1L);
    }
}