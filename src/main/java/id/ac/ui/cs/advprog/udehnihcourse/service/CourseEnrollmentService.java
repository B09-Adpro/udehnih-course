package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.clients.PaymentServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.exception.AlreadyEnrolledException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.CourseNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.EnrollmentNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.PaymentInitiationFailedException;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.*;
import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import id.ac.ui.cs.advprog.udehnihcourse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CourseEnrollmentService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final AuthServiceClient authServiceClient;

    @Value("${payment.service.api.key}")
    private String paymentServiceApiKey;

    public EnrollmentDTO enrollStudentInCourse(Long studentId, Long courseId, String paymentMethod ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatusEquals(studentId, courseId, EnrollmentStatus.ENROLLED)) {
            throw new AlreadyEnrolledException("Student is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();

                
        enrollment = enrollmentRepository.save(enrollment);

        String tutorName = authServiceClient.getUserInfoById(course.getTutorId()).getName();
        boolean paymentInitiated = processPayment(studentId, courseId, course.getPrice(), paymentMethod, enrollment.getId(), course.getTitle(), tutorName);
        if (!paymentInitiated) {
            enrollment.setStatus(EnrollmentStatus.PAYMENT_FAILED);
            throw new PaymentInitiationFailedException("Gagal menginisiasi pembayaran");
        }

        return EnrollmentDTO.builder()
                .enrollmentId(enrollment.getId())
                .message("Pendaftaran kursus berhasil, menunggu verifikasi pembayaran")
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
                        .instructor(authServiceClient.getUserInfoById(enrollment.getCourse().getTutorId()).getName())
                        .build())
                .collect(Collectors.toList());

        return enrolledCourses;
    }

    public void processPaymentCallback(PaymentCallbackDTO callback) {
        Enrollment enrollment = enrollmentRepository.findById(callback.getEnrollmentId())
                .orElseThrow(() -> new EnrollmentNotFoundException("Pendaftaran tidak ditemukan"));

        if (callback.isApproved()) {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        } else {
            enrollment.setStatus(EnrollmentStatus.PAYMENT_FAILED);
        }
    }

    private boolean processPayment(Long studentId, Long courseId, BigDecimal price, String paymentMethod, Long enrollmentId, String courseTitle, String tutorName) {
        try {
            PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                    .enrollmentId(enrollmentId)
                    .studentId(studentId)
                    .courseId(courseId)
                    .courseTitle(courseTitle)
                    .tutorName(tutorName)
                    .amount(price)
                    .paymentMethod(paymentMethod)
                    .timestamp(System.currentTimeMillis())
                    .build();

            PaymentResponseDTO response = paymentServiceClient.createPaymentRequest(paymentServiceApiKey, paymentRequest);

            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
}