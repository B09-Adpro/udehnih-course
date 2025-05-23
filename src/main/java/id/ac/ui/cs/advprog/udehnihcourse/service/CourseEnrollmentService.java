package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.exception.AlreadyEnrolledException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.CourseNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.EnrollmentNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.PaymentInitiationFailedException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.util.Map;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.*;
import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import id.ac.ui.cs.advprog.udehnihcourse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.out;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseEnrollmentService {

    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${payment.service.url:http://localhost:8080}")
    private String paymentServiceUrl;

    public EnrollmentDTO enrollStudentInCourse(Long studentId, Long courseId, String paymentMethod ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AlreadyEnrolledException("Student is already enrolled in this course");
        }

        boolean paymentInitiated = processPayment(studentId, courseId, course.getPrice(), paymentMethod);
        if (!paymentInitiated) {
            throw new PaymentInitiationFailedException("Gagal menginisiasi pembayaran");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .course(course)
                .status(EnrollmentStatus.PENDING)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        return EnrollmentDTO.builder()
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
                        .instructor(getTutorName(enrollment.getCourse().getTutorId()))
                        .build())
                .collect(Collectors.toList());

        return enrolledCourses;
    }

    public void processPaymentCallback(PaymentCallbackDTO callback) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(
                        callback.getStudentId(), callback.getCourseId())
                .orElseThrow(() -> new EnrollmentNotFoundException("Pendaftaran tidak ditemukan"));

        if (callback.isApproved()) {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
        } else {
            enrollment.setStatus(EnrollmentStatus.PAYMENT_FAILED);
        }
    }

    private boolean processPayment(Long studentId, Long courseId, BigDecimal price, String paymentMethod) {
        try {
            PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                    .studentId(studentId)
                    .courseId(courseId)
                    .amount(price)
                    .paymentMethod(paymentMethod)
                    .timestamp(System.currentTimeMillis())
                    .build();

            PaymentResponseDTO response = restTemplate.postForObject(
                    paymentServiceUrl + "/api/payments/process",
                    paymentRequest,
                    PaymentResponseDTO.class
            );

            return response != null && response.isSuccess();
        } catch (Exception e) {
            out.println(e);
            return false;
        }
    }

    // TODO : Implement this method to fetch the tutor name based on the tutorId
    private String getTutorName(String tutorId) {
        // Place Holder for actual implementation
        return "Tutor Name";
    }
}