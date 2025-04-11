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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseListDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseBrowsingService;


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
    private final CourseBrowsingService courseBrowsingService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseCreateRequest createRequest) {
        // TODO: Get authenticated Tutor ID from Security Context
        String tutorId = "tutor-test";

        CourseResponse response = courseManagementService.createCourse(createRequest, tutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long courseId, @RequestBody CourseUpdateRequest updateRequest) {
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

    
    @GetMapping
    public ResponseEntity<List<CourseListDTO>> getAllCourses() {
        List<CourseListDTO> courses = courseBrowsingService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseListDTO>> searchCourses(String keyword) {
        List<CourseListDTO> courses = courseBrowsingService.searchCourses(keyword);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailDTO> getCourseById(Long id) {
        CourseDetailDTO course = courseBrowsingService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
}
