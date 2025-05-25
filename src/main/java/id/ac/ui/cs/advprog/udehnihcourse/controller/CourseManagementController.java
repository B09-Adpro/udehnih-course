package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.course.*;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.ArticleDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.SectionDTO;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseDetailResponse;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseListDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseBrowsingService;
import lombok.RequiredArgsConstructor;


import java.util.HashMap;
import java.net.URI;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseById(
            @PathVariable Long courseId,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {
        String tutorId = String.valueOf(tutorDetails.getId());
        CourseDetailResponse response = courseManagementService.getCourseDetailById(courseId, tutorId);
        return ResponseEntity.ok(response);
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

    @GetMapping
    public ResponseEntity<Map<String, List<CourseListDTO>>> getAllCourses() {
        List<CourseListDTO> courses = courseBrowsingService.getAllCourses();

        Map<String, List<CourseListDTO>> response = new HashMap<>();
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("search")
    public ResponseEntity<Map<String, List<CourseListDTO>>> searchCourses(@RequestParam String keyword) {
        List<CourseListDTO> courses = courseBrowsingService.searchCourses(keyword);

        Map<String, List<CourseListDTO>> response = new HashMap<>();
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{courseId}")
    public ResponseEntity<CourseDetailDTO> getCourseById(@PathVariable Long courseId) {
        CourseDetailDTO course = courseBrowsingService.getCourseById(courseId, null);
        return ResponseEntity.ok(course);
    }

    @GetMapping("{courseId}/content")
    public ResponseEntity<CourseDetailDTO> getCourseContent(
            @PathVariable Long courseId,
            @AuthenticationPrincipal AppUserDetails userDetails) {
        Long studentId = userDetails.getId();
        if (studentId == null) {
            throw new RuntimeException("Invalid authentication token");
        }
        CourseDetailDTO course = courseBrowsingService.getCourseById(courseId, studentId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("{courseId}/sections/{sectionId}")
    public ResponseEntity<SectionDTO> getSectionContent(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal AppUserDetails userDetails) {

        Long studentId = userDetails.getId();
        if (studentId == null) {
            throw new RuntimeException("Invalid authentication token");
        }

        SectionDTO section = courseBrowsingService.getSectionById(courseId, sectionId, studentId);
        return ResponseEntity.ok(section);
    }

    @GetMapping("{courseId}/sections/{sectionId}/articles/{articleId}")
    public ResponseEntity<ArticleDTO> getArticleContent(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long articleId,
            @AuthenticationPrincipal AppUserDetails userDetails) {

        Long studentId = userDetails.getId();
        if (studentId == null) {
            throw new RuntimeException("Invalid authentication token");
        }

        ArticleDTO article = courseBrowsingService.getArticleById(courseId, articleId, studentId);
        return ResponseEntity.ok(article);
    }
}
