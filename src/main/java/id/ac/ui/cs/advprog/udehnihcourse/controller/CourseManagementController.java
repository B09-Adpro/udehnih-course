package id.ac.ui.cs.advprog.udehnihcourse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<CourseDetailDTO> getCourseById(Long id) {
        CourseDetailDTO course = courseBrowsingService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
}
