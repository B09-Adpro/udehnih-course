package id.ac.ui.cs.advprog.udehnihcourse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.udehnihcourse.model.Article;
import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Section;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseDetailDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.CourseListDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.SectionDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.ArticleDTO;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseBrowsingService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    public List<CourseListDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
            .map(this::convertToDto)
            .toList();
    }

    public List<CourseListDTO> searchCourses(String keyword) {
        List<Course> courses = courseRepository.findByTitleContainingIgnoreCase(keyword);
        return courses.stream()
            .map(this::convertToDto)
            .toList();
    }

    public CourseDetailDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));   
    
        return CourseDetailDTO.builder()
            .id(course.getId())
            .title(course.getTitle())
            .description(course.getDescription())
            .tutorName(getTutorName(course.getTutorId()))
            .price(course.getPrice())
            .sections(mapToSectionDTOs(course.getSections()))
            .build();
    }

    private CourseListDTO convertToDto(Course course) {
        CourseListDTO dto = CourseListDTO.builder()
            .id(course.getId())
            .title(course.getTitle())
            .price(course.getPrice())
            .tutorName(getTutorName(course.getTutorId()))
            .build();
        return dto; 
    }

    private List<SectionDTO> mapToSectionDTOs(List<Section> sections) {
        return Optional.ofNullable(sections).orElse(Collections.emptyList()).stream()
            .map(this::mapToSectionDTO)
            .toList();
    }

    private SectionDTO mapToSectionDTO(Section section) {
        return SectionDTO.builder()
            .id(section.getId())
            .title(section.getTitle())
            .articles(mapToArticleDTOs(section.getArticles()))
            .build();
    }

    private List<ArticleDTO> mapToArticleDTOs(List<Article> articles) {
        return Optional.ofNullable(articles).orElse(Collections.emptyList()).stream()
            .map(this::mapToArticleDTO)
            .toList();
    }

    private ArticleDTO mapToArticleDTO(Article article) {
        return ArticleDTO.builder()
            .id(article.getId())
            .title(article.getTitle())
            .content(article.getContent())
            .build();
    }

    // TODO : Implement this method to fetch the tutor name based on the tutorId
    private String getTutorName(String tutorId) {
        // Place Holder for actual implementation
        return "Tutor Name";
    }
}
