package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.ArticleDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.SectionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseListDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseBrowsingService;
import lombok.RequiredArgsConstructor;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseManagementController {
    
    private final CourseBrowsingService courseBrowsingService;
    
    @GetMapping
    public ResponseEntity<Map<String, List<CourseListDTO>>> getAllCourses() {
        List<CourseListDTO> courses = courseBrowsingService.getAllCourses();

        Map<String, List<CourseListDTO>> response = new HashMap<>();
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, List<CourseListDTO>>> searchCourses(@RequestParam String keyword) {
        List<CourseListDTO> courses = courseBrowsingService.searchCourses(keyword);

        Map<String, List<CourseListDTO>> response = new HashMap<>();
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailDTO> getCourseById(@PathVariable Long courseId) {
        CourseDetailDTO course = courseBrowsingService.getCourseById(courseId, null);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{courseId}/content")
    public ResponseEntity<CourseDetailDTO> getCourseContent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId) {
        Long studentId = extractStudentIdFromToken(token);
        if (studentId == null) {
            throw new RuntimeException("Invalid authentication token");
        }
        CourseDetailDTO course = courseBrowsingService.getCourseById(courseId, studentId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{courseId}/sections/{sectionId}")
    public ResponseEntity<SectionDTO> getSectionContent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId,
            @PathVariable Long sectionId) {

        Long studentId = extractStudentIdFromToken(token);
        if (studentId == null) {
            throw new RuntimeException("Invalid authentication token");
        }

        SectionDTO section = courseBrowsingService.getSectionById(courseId, sectionId, studentId);
        return ResponseEntity.ok(section);
    }

    @GetMapping("/{courseId}/sections/{sectionId}/articles/{articleId}")
    public ResponseEntity<ArticleDTO> getArticleContent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long articleId) {

        Long studentId = extractStudentIdFromToken(token);
        if (studentId == null) {
            throw new RuntimeException("Invalid authentication token");
        }

        ArticleDTO article = courseBrowsingService.getArticleById(courseId, articleId, studentId);
        return ResponseEntity.ok(article);
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
