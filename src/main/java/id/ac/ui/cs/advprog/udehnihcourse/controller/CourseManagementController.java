package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseCreateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseUpdateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseEnrollmentStudentDTO;
import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;

import java.net.URI;
import java.util.List;

/**
 * REST Controller for handling Course CRUD actions by authenticated Tutors.
 * Design Pattern: Controller (REST)
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseManagementController {

    private final CourseManagementService courseManagementService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseCreateRequest createRequest,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {
        String tutorId = String.valueOf(tutorDetails.getId());

        CourseResponse response = courseManagementService.createCourse(createRequest, tutorId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getCourseId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest updateRequest,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {
        String tutorId = String.valueOf(tutorDetails.getId());

        CourseResponse response = courseManagementService.updateCourse(courseId, updateRequest, tutorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<GenericResponse> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {
        String tutorId = String.valueOf(tutorDetails.getId());

        courseManagementService.deleteCourse(courseId, tutorId);
        return ResponseEntity.ok(new GenericResponse("Course deleted successfully"));
    }

    @GetMapping("/{courseId}/enrollments")
    public ResponseEntity<List<CourseEnrollmentStudentDTO>> getEnrolledStudents(
            @PathVariable Long courseId,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {
        String tutorId = String.valueOf(tutorDetails.getId());

        List<CourseEnrollmentStudentDTO> students = courseManagementService.getEnrolledStudentsForCourse(courseId, tutorId);
        return ResponseEntity.ok(students);
    }

    @PostMapping("/{courseId}/submit-review")
    public ResponseEntity<GenericResponse> submitMyCourseForReview(
            @PathVariable Long courseId,
            @AuthenticationPrincipal AppUserDetails tutorDetails) {
        if (tutorDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        String tutorId = String.valueOf(tutorDetails.getId());

        courseManagementService.submitCourseForReview(courseId, tutorId);
        return ResponseEntity.ok(new GenericResponse("Course with ID " + courseId + " submitted for review successfully."));
    }

}
