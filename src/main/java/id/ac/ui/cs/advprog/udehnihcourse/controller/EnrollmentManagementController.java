package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.*;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class EnrollmentManagementController {

    @Value("${services.payment.api-key}")
    private String paymentServiceApiKey;

    private final CourseEnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentDTO> enrollInCourse(
            @RequestParam Long courseId,
            @RequestParam(name = "payment_method") String paymentMethod,
            @AuthenticationPrincipal AppUserDetails userDetails){
        Long studentId = userDetails.getId();
        EnrollmentDTO enrollmentDTO = enrollmentService.enrollStudentInCourse(studentId, courseId, paymentMethod);
        return ResponseEntity.ok(enrollmentDTO);
    }

    @GetMapping
    public ResponseEntity<Map<String,  List<EnrolledCourseDTO>>> getEnrolledCourses(
            @AuthenticationPrincipal AppUserDetails userDetails) {
        Long studentId = userDetails.getId();
        List<EnrolledCourseDTO> enrolledCourse = enrollmentService.getStudentEnrollments(studentId);

        Map<String,  List<EnrolledCourseDTO>> response = new HashMap<>();
        response.put("courses", enrolledCourse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("payment-callback")
    public ResponseEntity<Map<String, Object>> handlePaymentCallback(
            @RequestBody PaymentCallbackDTO callback) {
        try {
            enrollmentService.processPaymentCallback(callback);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "received");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Terjadi kesalahan dalam memproses callback");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}