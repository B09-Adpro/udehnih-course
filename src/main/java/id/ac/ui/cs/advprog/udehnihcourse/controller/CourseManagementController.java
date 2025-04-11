package id.ac.ui.cs.advprog.udehnihcourse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseListDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseBrowsingService;
import lombok.RequiredArgsConstructor;


import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseManagementController {
    
    private final CourseBrowsingService courseBrowsingService;
    
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
