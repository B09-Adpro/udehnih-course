package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import id.ac.ui.cs.advprog.udehnihcourse.service.TutorRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

@Slf4j
@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorManagementController {

    private final TutorRegistrationService tutorRegistrationService;
    private final CourseManagementService courseManagementService;

    @PostMapping("/apply")
    public ResponseEntity<TutorApplicationResponse> applyAsTutor(
            @Valid @RequestBody TutorApplicationRequest request,
            @AuthenticationPrincipal AppUserDetails studentDetails
    ) {

        String studentId = String.valueOf(studentDetails.getId());

        TutorApplicationResponse response = tutorRegistrationService.applyAsTutor(request, studentId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/status")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/status")
    public ResponseEntity<TutorApplicationStatusResponse> checkApplicationStatus(
            @AuthenticationPrincipal AppUserDetails studentDetails
    ) {

        log.info("CONTROLLER: checkApplicationStatus called. Principal: {}", studentDetails);
        Authentication authInController = SecurityContextHolder.getContext().getAuthentication();
        log.info("CONTROLLER: Authentication from SecurityContextHolder: {}", authInController);

        String studentId = String.valueOf(studentDetails.getId());

        TutorApplicationStatusResponse response = tutorRegistrationService.checkApplicationStatus(studentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/apply")
    public ResponseEntity<GenericResponse> cancelTutorApplication(
            @AuthenticationPrincipal AppUserDetails studentDetails
    ) {

        String studentId = String.valueOf(studentDetails.getId());

        tutorRegistrationService.cancelTutorApplication(studentId);
        return ResponseEntity.ok(new GenericResponse("Tutor application canceled successfully"));
    }

    @GetMapping("/courses")
    public ResponseEntity<Map<String, Object>> getMyCourses(
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {

        String tutorId = String.valueOf(tutorDetails.getId());

        List<TutorCourseListItem> courses = courseManagementService.getCoursesByTutor(tutorId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("courses", courses);

        return ResponseEntity.ok(responseBody);
    }
}
