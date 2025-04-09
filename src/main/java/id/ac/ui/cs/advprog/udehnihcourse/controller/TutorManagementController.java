package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.service.TutorRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST Controller for handling Tutor application lifecycle by Students.
 * Design Pattern: Controller (REST)
 */
@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorManagementController {

    private final TutorRegistrationService tutorRegistrationService;

    @PostMapping("/apply")
    public ResponseEntity<TutorApplicationResponse> applyAsTutor(@RequestBody TutorApplicationRequest request) {
        // TODO: Get studentId from Security Context
        String studentId = "student-apply-placeholder";

        TutorApplicationResponse response = tutorRegistrationService.applyAsTutor(request, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/status")
    public ResponseEntity<TutorApplicationStatusResponse> checkApplicationStatus() {
        // TODO: Get studentId from Security Context
        String studentId = "student-status-placeholder";

        TutorApplicationStatusResponse response = tutorRegistrationService.checkApplicationStatus(studentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/apply")
    public ResponseEntity<GenericResponse> cancelTutorApplication() {
        // TODO: Get studentId from Security Context
        String studentId = "student-cancel-placeholder";

        tutorRegistrationService.cancelTutorApplication(studentId);
        return ResponseEntity.ok(new GenericResponse("Tutor application canceled successfully"));
    }
}
