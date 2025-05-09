package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import id.ac.ui.cs.advprog.udehnihcourse.service.TutorRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST Controller for handling Tutor application lifecycle by Students.
 * Design Pattern: Controller (REST)
 */
@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorManagementController {

    private final TutorRegistrationService tutorRegistrationService;
    private final CourseManagementService courseManagementService;

    @PostMapping("/apply")
    public ResponseEntity<TutorApplicationResponse> applyAsTutor(@Valid @RequestBody TutorApplicationRequest request) {
        // TODO: Get studentId from Security Context
        String studentId = "student-test";

        TutorApplicationResponse response = tutorRegistrationService.applyAsTutor(request, studentId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/status")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/status")
    public ResponseEntity<TutorApplicationStatusResponse> checkApplicationStatus() {
        // TODO: Get studentId from Security Context
        String studentId = "student-test";

        TutorApplicationStatusResponse response = tutorRegistrationService.checkApplicationStatus(studentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/apply")
    public ResponseEntity<GenericResponse> cancelTutorApplication() {
        // TODO: Get studentId from Security Context
        String studentId = "student-test";

        tutorRegistrationService.cancelTutorApplication(studentId);
        return ResponseEntity.ok(new GenericResponse("Tutor application canceled successfully"));
    }

    @GetMapping("/courses")
    public ResponseEntity<Map<String, Object>> getMyCourses() {
        // TODO: Get authenticated Tutor ID from Security Context
        String tutorId = "tutor-test";

        List<TutorCourseListItem> courses = courseManagementService.getCoursesByTutor(tutorId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("courses", courses);

        return ResponseEntity.ok(responseBody);
    }
}
