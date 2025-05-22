package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.*;
import id.ac.ui.cs.advprog.udehnihcourse.repository.ArticleRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.EnrollmentRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

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
    private final EnrollmentRepository enrollmentRepository;

    public SectionResponse addSectionToCourse(Long courseId, SectionRequest sectionRequest, String tutorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found with ID: " + courseId));
        courseManagementService.verifyCourseOwnership(course, tutorId);
        courseManagementService.verifyCourseIsModifiable(course);

        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Course cannot be modified while it is PENDING_REVIEW.");
        }

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
        courseManagementService.verifyCourseIsModifiable(course);

        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Course cannot be modified while it is PENDING_REVIEW.");
        }

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
        courseManagementService.verifyCourseIsModifiable(course);

        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Course cannot be modified while it is PENDING_REVIEW.");
        }

        verifyCanDeleteSectionFromPublishedCourse(course);

        Section section = sectionRepository.findByIdAndCourseId(sectionId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Section not found with ID: " + sectionId + " for course " + courseId));

        sectionRepository.delete(section);
    }

    public ArticleResponse addArticleToSection(Long sectionId, ArticleRequest articleRequest, String tutorId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Section not found with ID: " + sectionId));
        courseManagementService.verifyCourseOwnership(section.getCourse(), tutorId);

        Course course = section.getCourse();

        courseManagementService.verifyCourseIsModifiable(course);

        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Course cannot be modified while it is PENDING_REVIEW.");
        }

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

        Course course = section.getCourse();
        courseManagementService.verifyCourseIsModifiable(course);

        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Course cannot be modified while it is PENDING_REVIEW.");
        }

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

        Course course = section.getCourse();
        courseManagementService.verifyCourseIsModifiable(course);

        if (course.getStatus() == CourseStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Course cannot be modified while it is PENDING_REVIEW.");
        }

        verifyCanDeleteArticleFromPublishedCourse(section, course);

        Article article = articleRepository.findByIdAndSectionId(articleId, sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Article not found with ID: " + articleId + " for section " + sectionId));

        articleRepository.delete(article);
    }

    private void authorizeContentView(
            Course course,
            String userIdFromPrincipal,
            Collection<? extends GrantedAuthority> authorities
    ) {
        Long numericUserId;
        try {
            numericUserId = Long.parseLong(userIdFromPrincipal);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid user ID format in security principal.");
        }

        boolean isTutorOwner = course.getTutorId().equals(userIdFromPrincipal) &&
                authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));

        if (isTutorOwner) {
            return;
        }

        boolean isEnrolledAndPaymentCompletedStudent = enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                course.getId(),
                numericUserId,
                EnrollmentStatus.ENROLLED
        );

        if (!isEnrolledAndPaymentCompletedStudent) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to view content for this course. Please ensure your enrollment is completed and payment is successful.");
        }
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getSectionsByCourseForUser(Long courseId, String userIdFromPrincipal, Collection<? extends GrantedAuthority> authorities) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId));

        authorizeContentView(course, userIdFromPrincipal, authorities);

        List<Section> sections = sectionRepository.findByCourseId(courseId);
        return sections.stream().map(SectionResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> getArticlesBySectionForUser(Long sectionId, String userIdFromPrincipal, Collection<? extends GrantedAuthority> authorities) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found with ID: " + sectionId));
        Course course = section.getCourse();
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Section is not associated with a course.");
        }

        authorizeContentView(course, userIdFromPrincipal, authorities);

        List<Article> articles = articleRepository.findBySectionId(sectionId);
        return articles.stream().map(ArticleResponse::fromEntity).collect(Collectors.toList());
    }

    private void verifyCanDeleteSectionFromPublishedCourse(Course course) {
        if (course.getStatus() == CourseStatus.PUBLISHED) {
            long totalSectionsInCourse = sectionRepository.countByCourseId(course.getId());
            if (totalSectionsInCourse <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot delete the last section of a PUBLISHED course. A published course must have at least one section.");
            }
        }
    }

    private void verifyCanDeleteArticleFromPublishedCourse(Section section, Course course) {
        if (course.getStatus() == CourseStatus.PUBLISHED) {
            long totalArticlesInSection = articleRepository.countBySectionId(section.getId());
            if (totalArticlesInSection <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot delete the last article of a section in a PUBLISHED course. A section in a published course must have at least one article.");
            }
        }
    }
}