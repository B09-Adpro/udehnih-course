package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.exception.ArticleNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.CourseNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.SectionNotFoundException;
import id.ac.ui.cs.advprog.udehnihcourse.exception.UnauthorizedAccessException;
import id.ac.ui.cs.advprog.udehnihcourse.model.EnrollmentStatus;
import id.ac.ui.cs.advprog.udehnihcourse.repository.EnrollmentRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseBrowsingService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthServiceClient authServiceClient;
    
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

    public CourseDetailDTO getCourseById(Long id, Long studentId) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        boolean isEnrolled = isEnrolled(studentId, id);
        String tutorName = authServiceClient.getUserInfoById(course.getTutorId()).getName();
        return CourseDetailDTO.builder()
            .id(course.getId())
            .title(course.getTitle())
            .category(course.getCategory())
            .instructor(tutorName)
            .price(course.getPrice())
            .is_free(isFree(course))
            .description(course.getDescription())
            .created_at(course.getCreatedAt().toString())
            .updated_at(course.getUpdatedAt().toString())
            .sections(isEnrolled ? mapToSectionDTOs(course.getSections()) : Collections.emptyList())
            .build();
    }

    public SectionDTO getSectionById(Long courseId, Long sectionId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!isEnrolled(studentId, courseId)) {
            throw new UnauthorizedAccessException("Student is not enrolled in this course");
        }

        Section section = course.getSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new SectionNotFoundException("Section not found in this course"));

        AtomicLong sectionOrder = new AtomicLong(1);
        for (Section s : course.getSections()) {
            if (s.getId().equals(sectionId)) {
                break;
            }
            sectionOrder.incrementAndGet();
        }

        return mapToSectionDTO(section, sectionOrder.get());
    }

    public ArticleDTO getArticleById(Long courseId, Long articleId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!isEnrolled(studentId, courseId)) {
            throw new UnauthorizedAccessException("Student is not enrolled in this course");
        }

        for (Section section : course.getSections()) {
            Optional<Article> articleOpt = section.getArticles().stream()
                    .filter(a -> a.getId().equals(articleId))
                    .findFirst();

            if (articleOpt.isPresent()) {
                Article article = articleOpt.get();

                AtomicLong counter = new AtomicLong(1);
                for (Article a : section.getArticles()) {
                    if (a.getId().equals(articleId)) {
                        break;
                    }
                    counter.incrementAndGet();
                }

                return mapToArticleDTO(article, counter.get());
            }
        }

        throw new ArticleNotFoundException("Article not found in this course");
    }

    private CourseListDTO convertToDto(Course course) {
        String tutorName = authServiceClient.getUserInfoById(course.getTutorId()).getName();
        CourseListDTO dto = CourseListDTO.builder()
            .id(course.getId())
            .title(course.getTitle())
            .category(course.getCategory())
            .instructor(tutorName)
            .price(course.getPrice())
            .build();
        return dto; 
    }

    private List<SectionDTO> mapToSectionDTOs(List<Section> sections) {
        AtomicLong counter = new AtomicLong(1);
        return Optional.ofNullable(sections).orElse(Collections.emptyList()).stream()
                .map(section -> mapToSectionDTO(section, counter.getAndIncrement()))
                .toList();
    }

    private SectionDTO mapToSectionDTO(Section section, Long order) {
        return SectionDTO.builder()
                .id(section.getId())
                .title(section.getTitle())
                .order(order)
                .articles(mapToArticleDTOs(section.getArticles()))
                .build();
    }

    private List<ArticleDTO> mapToArticleDTOs(List<Article> articles) {
        AtomicLong counter = new AtomicLong(1);
        return Optional.ofNullable(articles).orElse(Collections.emptyList()).stream()
            .map(article -> mapToArticleDTO(article, counter.getAndIncrement()))
            .toList();
    }

    private ArticleDTO mapToArticleDTO(Article article, Long order) {
        return ArticleDTO.builder()
            .id(article.getId())
            .title(article.getTitle())
            .content_Type(article.getContentType())
            .content(article.getContent())
            .order(order)
            .build();
    }

    private boolean isFree(Course course) {
        return course.getPrice().compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isEnrolled(Long studentId, Long courseId) {
        
        return enrollmentRepository.existsByStudentIdAndCourseIdAndStatusEquals(studentId, courseId, EnrollmentStatus.ENROLLED.name());
    }
}
