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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseEnrollmentStudentDTO;
import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest createRequest) {
        // TODO: Get authenticated Tutor ID from Security Context
        String tutorId = "tutor-test";

        CourseResponse response = courseManagementService.createCourse(createRequest, tutorId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getCourseId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

//    @GetMapping("/tutor/courses")
//    public ResponseEntity<List<TutorCourseListItem>> getMyCourses() {
//        // TODO: Get authenticated Tutor ID from Security Context
//        String tutorId = "tutor-test";
//
//        List<TutorCourseListItem> courses = courseManagementService.getCoursesByTutor(tutorId);
//        return ResponseEntity.ok(courses);
//    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long courseId, @Valid @RequestBody CourseUpdateRequest updateRequest) {
        // TODO: Get authenticated Tutor ID from Security Context
        String tutorId = "tutor-test";

        CourseResponse response = courseManagementService.updateCourse(courseId, updateRequest, tutorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<GenericResponse> deleteCourse(@PathVariable Long courseId) {
        // TODO: Get authenticated Tutor ID from Security Context
        String tutorId = "tutor-test";

        courseManagementService.deleteCourse(courseId, tutorId);
        return ResponseEntity.ok(new GenericResponse("Course deleted successfully"));
    }

    @GetMapping("/{courseId}/enrollments")
    public ResponseEntity<List<CourseEnrollmentStudentDTO>> getEnrolledStudents(
            @PathVariable Long courseId) {
        // TODO: Get authenticated Tutor ID from Security Context
        String tutorId = "tutor-test";

        List<CourseEnrollmentStudentDTO> students = courseManagementService.getEnrolledStudentsForCourse(courseId, tutorId);
        return ResponseEntity.ok(students);
    }

}
