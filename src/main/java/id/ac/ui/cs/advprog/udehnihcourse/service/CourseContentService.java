package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.Article;
import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Section;
import id.ac.ui.cs.advprog.udehnihcourse.repository.ArticleRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Service for managing Course content (Sections and Articles) by Tutors.
 * Design Pattern: Service Layer
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CourseContentService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final ArticleRepository articleRepository;
    private final CourseManagementService courseManagementService;

    public SectionResponse addSectionToCourse(Long courseId, SectionRequest sectionRequest, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found with ID: " + courseId));
        courseManagementService.verifyCourseOwnership(course, tutorId);

        Section section = new Section();
        section.setTitle(sectionRequest.getTitle());
        // course.addSection(section);
        section.setCourse(course);

        Section savedSection = sectionRepository.save(section);
        // courseRepository.save(course);
        return SectionResponse.fromEntity(savedSection);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getSectionsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found with ID: " + courseId);
        }
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        return sections.stream().map(SectionResponse::fromEntity).collect(Collectors.toList());
    }


    public SectionResponse updateSection(Long courseId, Long sectionId, SectionRequest sectionRequest, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found with ID: " + courseId));
        courseManagementService.verifyCourseOwnership(course, tutorId);

        Section section = sectionRepository.findByIdAndCourseId(sectionId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found with ID: " + sectionId + " for course " + courseId));

        section.setTitle(sectionRequest.getTitle());
        Section updatedSection = sectionRepository.save(section);
        return SectionResponse.fromEntity(updatedSection);
    }

    public void deleteSection(Long courseId, Long sectionId, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found with ID: " + courseId));
        courseManagementService.verifyCourseOwnership(course, tutorId);

        Section section = sectionRepository.findByIdAndCourseId(sectionId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Section not found with ID: " + sectionId + " for course " + courseId));

        sectionRepository.delete(section);
    }

    public ArticleResponse addArticleToSection(Long sectionId, ArticleRequest articleRequest, String tutorId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Section not found with ID: " + sectionId));
        courseManagementService.verifyCourseOwnership(section.getCourse(), tutorId);

        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setContentType(articleRequest.getContentType());
        // section.addArticle(article);
        article.setSection(section);

        Article savedArticle = articleRepository.save(article);
        // sectionRepository.save(section);
        return ArticleResponse.fromEntity(savedArticle);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesBySection(Long sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Section not found with ID: " + sectionId);
        }
        List<Article> articles = articleRepository.findBySectionId(sectionId);
        return articles.stream().map(ArticleResponse::fromEntity).collect(Collectors.toList());
    }


    public ArticleResponse updateArticle(Long sectionId, Long articleId, ArticleRequest articleRequest, String tutorId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Section not found with ID: " + sectionId));
        courseManagementService.verifyCourseOwnership(section.getCourse(), tutorId);

        Article article = articleRepository.findByIdAndSectionId(articleId, sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Article not found with ID: " + articleId + " for section " + sectionId));

        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setContentType(articleRequest.getContentType());
        Article updatedArticle = articleRepository.save(article);
        return ArticleResponse.fromEntity(updatedArticle);
    }

    public void deleteArticle(Long sectionId, Long articleId, String tutorId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Section not found with ID: " + sectionId));
        courseManagementService.verifyCourseOwnership(section.getCourse(), tutorId);

        Article article = articleRepository.findByIdAndSectionId(articleId, sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Article not found with ID: " + articleId + " for section " + sectionId));

        articleRepository.delete(article);
    }
}