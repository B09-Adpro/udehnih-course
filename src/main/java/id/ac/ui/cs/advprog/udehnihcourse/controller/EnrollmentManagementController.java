package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.*;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class EnrollmentManagementController {

    private final CourseEnrollmentService enrollmentService;

    // TODO : Exception Handling for Already Enrolled, Unauthorized JWT, Courses Not Found (CHECK API DOCS)
    @PostMapping
    public ResponseEntity<EnrollmentDTO> enrollInCourse(
            @RequestHeader("Authorization") String token,
            @RequestParam Long courseId) {
        String studentId = extractStudentIdFromToken(token);
        EnrollmentDTO enrollmentDTO = enrollmentService.enrollStudentInCourse(studentId, courseId);
        return ResponseEntity.ok(enrollmentDTO);
    }

    // TODO : Exception Handling for Unauthorized JWT (CHECK API DOCS)
    @GetMapping
    public ResponseEntity<Map<String,  List<EnrolledCourseDTO>>> getEnrolledCourses(
            @RequestHeader("Authorization") String token) {
        String studentId = extractStudentIdFromToken(token);
        List<EnrolledCourseDTO> enrolledCourse = enrollmentService.getStudentEnrollments(studentId);

        Map<String,  List<EnrolledCourseDTO>> response = new HashMap<>();
        response.put("courses", enrolledCourse);

        return ResponseEntity.ok(response);
    }

    // TODO : Implement JWT Token Auth
    private String extractStudentIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return "dummy-student-id";
    }
}