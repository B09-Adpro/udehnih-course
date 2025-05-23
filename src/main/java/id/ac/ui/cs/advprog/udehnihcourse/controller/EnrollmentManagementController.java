package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.*;
import id.ac.ui.cs.advprog.udehnihcourse.exception.EnrollmentNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // TODO : Exception Handling for Already Enrolled, Unauthorized JWT, Courses Not Found (CHECK API DOCS)
    @PostMapping
    public ResponseEntity<EnrollmentDTO> enrollInCourse(
            @RequestHeader("Authorization") String token,
            @RequestParam Long courseId,
            @RequestParam(name = "payment_method") String paymentMethod) {
        Long studentId = extractStudentIdFromToken(token);
        EnrollmentDTO enrollmentDTO = enrollmentService.enrollStudentInCourse(studentId, courseId, paymentMethod);
        return ResponseEntity.ok(enrollmentDTO);
    }

    // TODO : Exception Handling for Unauthorized JWT (CHECK API DOCS)
    @GetMapping
    public ResponseEntity<Map<String,  List<EnrolledCourseDTO>>> getEnrolledCourses(
            @RequestHeader("Authorization") String token) {
        Long studentId = extractStudentIdFromToken(token);
        List<EnrolledCourseDTO> enrolledCourse = enrollmentService.getStudentEnrollments(studentId);

        Map<String,  List<EnrolledCourseDTO>> response = new HashMap<>();
        response.put("courses", enrolledCourse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment-callback")
    public ResponseEntity<Map<String, Object>> handlePaymentCallback(
            @RequestHeader(value = "X-API-Key", required = true) String apiKey,
            @RequestBody PaymentCallbackDTO callback) {

        if (!validatePaymentServiceApiKey(apiKey)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

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

    private boolean validatePaymentServiceApiKey(String apiKey) {
        return paymentServiceApiKey != null && paymentServiceApiKey.equals(apiKey);
    }

    // TODO : Implement JWT Token Auth
    private Long extractStudentIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // Placeholder: returning a dummy Long ID instead of String
        return 12345L;
    }
}