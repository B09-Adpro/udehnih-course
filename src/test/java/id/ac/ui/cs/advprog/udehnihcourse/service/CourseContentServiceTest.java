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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseContentServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CourseManagementService courseManagementService;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseContentService courseContentService;

    private Course course;
    private Course pendingReviewCourse;
    private Course publishedCourse;
    private Section section;
    private Section sectionWithMultipleArticles;
    private Article article;
    private Article article2;
    private String tutorId;
    private String studentId;
    private Long courseId;
    private Long sectionId;
    private Long articleId;
    private Collection<GrantedAuthority> tutorAuthorities;
    private Collection<GrantedAuthority> studentAuthorities;

    @BeforeEach
    void setUp() {
        tutorId = "123"; // Changed to numeric string to avoid NumberFormatException
        studentId = "456"; // Changed to numeric string to avoid NumberFormatException
        courseId = 1L;
        sectionId = 10L;
        articleId = 100L;

        tutorAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TUTOR"));
        studentAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));

        course = Course.builder()
                .id(courseId)
                .title("Test Course")
                .tutorId(tutorId)
                .status(CourseStatus.DRAFT)
                .build();

        pendingReviewCourse = Course.builder()
                .id(courseId)
                .title("Pending Review Course")
                .tutorId(tutorId)
                .status(CourseStatus.PENDING_REVIEW)
                .build();

        publishedCourse = Course.builder()
                .id(courseId)
                .title("Published Course")
                .tutorId(tutorId)
                .status(CourseStatus.PUBLISHED)
                .build();

        section = new Section();
        section.setId(sectionId);
        section.setTitle("Test Section");
        section.setCourse(course);

        article = new Article();
        article.setId(articleId);
        article.setTitle("Test Article");
        article.setContent("Content");
        article.setContentType("TEXT");
        article.setSection(section);

        article2 = new Article();
        article2.setId(articleId + 1);
        article2.setTitle("Test Article 2");
        article2.setContent("Content 2");
        article2.setContentType("TEXT");

        sectionWithMultipleArticles = new Section();
        sectionWithMultipleArticles.setId(sectionId);
        sectionWithMultipleArticles.setTitle("Section with multiple articles");
        sectionWithMultipleArticles.setCourse(publishedCourse);
        sectionWithMultipleArticles.setArticles(Arrays.asList(article, article2));
    }

    // SECTION TESTS

    @Test
    void addSectionToCourse_whenCourseExistsAndTutorOwns_shouldCreateSection() {
        SectionRequest request = new SectionRequest();
        request.setTitle("New Section Title");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseManagementService).verifyCourseOwnership(course, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(sectionRepository.save(any(Section.class))).thenAnswer(invocation -> {
            Section s = invocation.getArgument(0);
            s.setId(sectionId + 1);
            s.setCourse(course);
            return s;
        });

        SectionResponse response = courseContentService.addSectionToCourse(courseId, request, tutorId);

        assertNotNull(response);
        assertEquals("New Section Title", response.getTitle());
        assertEquals(courseId, response.getCourseId());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseManagementService, times(1)).verifyCourseOwnership(course, tutorId);
        verify(courseManagementService, times(1)).verifyCourseIsModifiable(course);
        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    void addSectionToCourse_whenCourseNotFound_shouldThrowNotFound() {
        SectionRequest request = new SectionRequest();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.addSectionToCourse(courseId, request, tutorId);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(courseManagementService, never()).verifyCourseOwnership(any(), anyString());
    }

    @Test
    void addSectionToCourse_whenCoursePendingReview_shouldThrowForbidden() {
        SectionRequest request = new SectionRequest();
        request.setTitle("New Section");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(pendingReviewCourse));
        doNothing().when(courseManagementService).verifyCourseOwnership(pendingReviewCourse, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(pendingReviewCourse);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.addSectionToCourse(courseId, request, tutorId);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("cannot be modified while it is PENDING_REVIEW"));
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    void getSectionsByCourse_whenCourseExists_shouldReturnSections() {
        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(sectionRepository.findByCourseId(courseId)).thenReturn(Collections.singletonList(section));

        List<SectionResponse> responses = courseContentService.getSectionsByCourse(courseId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(section.getTitle(), responses.get(0).getTitle());
        verify(sectionRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    void getSectionsByCourse_whenCourseNotExists_shouldThrowNotFound() {
        when(courseRepository.existsById(courseId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getSectionsByCourse(courseId);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(sectionRepository, never()).findByCourseId(anyLong());
    }

    @Test
    void getSectionsByCourseForUser_whenTutorOwner_shouldReturnSections() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(sectionRepository.findByCourseId(courseId)).thenReturn(Collections.singletonList(section));

        List<SectionResponse> responses = courseContentService.getSectionsByCourseForUser(courseId, tutorId, tutorAuthorities);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(courseRepository, times(1)).findById(courseId);
        verify(sectionRepository, times(1)).findByCourseId(courseId);
        // Tutor owner should not need enrollment check
        verify(enrollmentRepository, never()).existsByCourseIdAndStudentIdAndStatus(any(), any(), any());
    }

    @Test
    void getSectionsByCourseForUser_whenEnrolledStudent_shouldReturnSections() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(courseId, 456L, EnrollmentStatus.ENROLLED)).thenReturn(true);
        when(sectionRepository.findByCourseId(courseId)).thenReturn(Collections.singletonList(section));

        List<SectionResponse> responses = courseContentService.getSectionsByCourseForUser(courseId, studentId, studentAuthorities);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(enrollmentRepository, times(1)).existsByCourseIdAndStudentIdAndStatus(courseId, 456L, EnrollmentStatus.ENROLLED);
        verify(sectionRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    void getSectionsByCourseForUser_whenUnenrolledStudent_shouldThrowForbidden() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(courseId, 456L, EnrollmentStatus.ENROLLED)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getSectionsByCourseForUser(courseId, studentId, studentAuthorities);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("not authorized to view content"));
        verify(sectionRepository, never()).findByCourseId(anyLong());
    }

    @Test
    void updateSection_whenValid_shouldUpdateAndReturnSection() {
        SectionRequest request = new SectionRequest();
        request.setTitle("Updated Section Title");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseManagementService).verifyCourseOwnership(course, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId)).thenReturn(Optional.of(section));
        when(sectionRepository.save(any(Section.class))).thenReturn(section);

        SectionResponse response = courseContentService.updateSection(courseId, sectionId, request, tutorId);

        assertNotNull(response);
        assertEquals("Updated Section Title", section.getTitle());
        assertEquals(section.getTitle(), response.getTitle());
        verify(sectionRepository, times(1)).save(section);
    }

    @Test
    void updateSection_whenSectionNotFound_shouldThrowNotFound() {
        SectionRequest request = new SectionRequest();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseManagementService).verifyCourseOwnership(course, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.updateSection(courseId, sectionId, request, tutorId);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(sectionRepository, never()).save(any(Section.class));
    }

    @Test
    void deleteSection_whenValid_shouldDeleteSection() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseManagementService).verifyCourseOwnership(course, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId)).thenReturn(Optional.of(section));
        doNothing().when(sectionRepository).delete(section);

        assertDoesNotThrow(() -> courseContentService.deleteSection(courseId, sectionId, tutorId));
        verify(sectionRepository, times(1)).delete(section);
    }

    @Test
    void deleteSection_whenPublishedCourseHasOnlyOneSection_shouldThrowBadRequest() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(publishedCourse));
        doNothing().when(courseManagementService).verifyCourseOwnership(publishedCourse, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(publishedCourse);

        when(sectionRepository.countByCourseId(courseId)).thenReturn(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.deleteSection(courseId, sectionId, tutorId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Cannot delete the last section"));

        verify(sectionRepository, never()).delete(any(Section.class));
        verify(sectionRepository, times(1)).countByCourseId(courseId);
    }

    @Test
    void deleteSection_whenPublishedCourseWithMultipleSections_shouldSucceed() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(publishedCourse));
        doNothing().when(courseManagementService).verifyCourseOwnership(publishedCourse, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(publishedCourse);
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId)).thenReturn(Optional.of(section));
        when(sectionRepository.countByCourseId(courseId)).thenReturn(2L);
        doNothing().when(sectionRepository).delete(section);

        assertDoesNotThrow(() -> courseContentService.deleteSection(courseId, sectionId, tutorId));
        verify(sectionRepository, times(1)).delete(section);
    }

    // ARTICLE TESTS

    @Test
    void addArticleToSection_whenSectionExistsAndTutorOwns_shouldCreateArticle() {
        ArticleRequest request = new ArticleRequest();
        request.setTitle("New Article Title");
        request.setContent("New Content");
        request.setContentType("TEXT");

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        doNothing().when(courseManagementService).verifyCourseOwnership(section.getCourse(), tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article a = invocation.getArgument(0);
            a.setId(articleId + 1);
            a.setSection(section);
            return a;
        });

        ArticleResponse response = courseContentService.addArticleToSection(sectionId, request, tutorId);

        assertNotNull(response);
        assertEquals("New Article Title", response.getTitle());
        assertEquals(sectionId, response.getSectionId());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void addArticleToSection_whenSectionNotFound_shouldThrowNotFound() {
        ArticleRequest request = new ArticleRequest();
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.addArticleToSection(sectionId, request, tutorId);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void addArticleToSection_whenCoursePendingReview_shouldThrowForbidden() {
        ArticleRequest request = new ArticleRequest();
        Section pendingSection = new Section();
        pendingSection.setId(sectionId);
        pendingSection.setCourse(pendingReviewCourse);

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(pendingSection));
        doNothing().when(courseManagementService).verifyCourseOwnership(pendingReviewCourse, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(pendingReviewCourse);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.addArticleToSection(sectionId, request, tutorId);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void getArticlesBySection_whenSectionExists_shouldReturnArticles() {
        when(sectionRepository.existsById(sectionId)).thenReturn(true);
        when(articleRepository.findBySectionId(sectionId)).thenReturn(Collections.singletonList(article));

        List<ArticleResponse> responses = courseContentService.getArticlesBySection(sectionId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(article.getTitle(), responses.get(0).getTitle());
        verify(articleRepository, times(1)).findBySectionId(sectionId);
    }

    @Test
    void getArticlesBySection_whenSectionNotExists_shouldThrowNotFound() {
        when(sectionRepository.existsById(sectionId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getArticlesBySection(sectionId);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(articleRepository, never()).findBySectionId(anyLong());
    }

    @Test
    void getArticlesBySectionForUser_whenTutorOwner_shouldReturnArticles() {
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(articleRepository.findBySectionId(sectionId)).thenReturn(Collections.singletonList(article));

        List<ArticleResponse> responses = courseContentService.getArticlesBySectionForUser(sectionId, tutorId, tutorAuthorities);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(sectionRepository, times(1)).findById(sectionId);
        verify(articleRepository, times(1)).findBySectionId(sectionId);
    }

    @Test
    void getArticlesBySectionForUser_whenEnrolledStudent_shouldReturnArticles() {
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(courseId, 456L, EnrollmentStatus.ENROLLED)).thenReturn(true);
        when(articleRepository.findBySectionId(sectionId)).thenReturn(Collections.singletonList(article));

        List<ArticleResponse> responses = courseContentService.getArticlesBySectionForUser(sectionId, studentId, studentAuthorities);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(enrollmentRepository, times(1)).existsByCourseIdAndStudentIdAndStatus(courseId, 456L, EnrollmentStatus.ENROLLED);
        verify(articleRepository, times(1)).findBySectionId(sectionId);
    }

    @Test
    void getArticlesBySectionForUser_whenUnenrolledStudent_shouldThrowForbidden() {
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(courseId, 456L, EnrollmentStatus.ENROLLED)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getArticlesBySectionForUser(sectionId, studentId, studentAuthorities);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(articleRepository, never()).findBySectionId(anyLong());
    }

    @Test
    void getArticlesBySectionForUser_whenSectionNotFound_shouldThrowNotFound() {
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getArticlesBySectionForUser(sectionId, studentId, studentAuthorities);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(articleRepository, never()).findBySectionId(anyLong());
    }

    @Test
    void getArticlesBySectionForUser_whenSectionHasNoCourse_shouldThrowInternalServerError() {
        Section orphanSection = new Section();
        orphanSection.setId(sectionId);
        orphanSection.setCourse(null);

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(orphanSection));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getArticlesBySectionForUser(sectionId, studentId, studentAuthorities);
        });
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getReason().contains("not associated with a course"));
    }

    @Test
    void updateArticle_whenValid_shouldUpdateAndReturnArticle() {
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Updated Article Title");
        request.setContent("Updated Content");
        request.setContentType("MARKDOWN");

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        doNothing().when(courseManagementService).verifyCourseOwnership(section.getCourse(), tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(articleRepository.findByIdAndSectionId(articleId, sectionId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        ArticleResponse response = courseContentService.updateArticle(sectionId, articleId, request, tutorId);

        assertNotNull(response);
        assertEquals("Updated Article Title", article.getTitle());
        assertEquals("Updated Content", article.getContent());
        assertEquals("MARKDOWN", article.getContentType());
        verify(articleRepository, times(1)).save(article);
    }

    @Test
    void updateArticle_whenArticleNotFound_shouldThrowNotFound() {
        ArticleRequest request = new ArticleRequest();
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        doNothing().when(courseManagementService).verifyCourseOwnership(section.getCourse(), tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(articleRepository.findByIdAndSectionId(articleId, sectionId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.updateArticle(sectionId, articleId, request, tutorId);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void deleteArticle_whenValid_shouldDeleteArticle() {
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        doNothing().when(courseManagementService).verifyCourseOwnership(section.getCourse(), tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(course);
        when(articleRepository.findByIdAndSectionId(articleId, sectionId)).thenReturn(Optional.of(article));
        doNothing().when(articleRepository).delete(article);

        assertDoesNotThrow(() -> courseContentService.deleteArticle(sectionId, articleId, tutorId));
        verify(articleRepository, times(1)).delete(article);
    }

    @Test
    void deleteArticle_whenPublishedCourseWithOnlyOneArticle_shouldThrowBadRequest() {
        Section publishedSection = new Section();
        publishedSection.setId(sectionId);
        publishedSection.setCourse(publishedCourse);

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(publishedSection));
        doNothing().when(courseManagementService).verifyCourseOwnership(publishedCourse, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(publishedCourse);
        when(articleRepository.countBySectionId(sectionId)).thenReturn(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.deleteArticle(sectionId, articleId, tutorId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Cannot delete the last article"));
        verify(articleRepository, never()).delete(any(Article.class));
        verify(articleRepository, times(1)).countBySectionId(sectionId);
        verify(articleRepository, never()).findByIdAndSectionId(anyLong(), anyLong());
    }

    @Test
    void deleteArticle_whenPublishedCourseWithMultipleArticles_shouldSucceed() {
        Section publishedSection = new Section();
        publishedSection.setId(sectionId);
        publishedSection.setCourse(publishedCourse);

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(publishedSection));
        doNothing().when(courseManagementService).verifyCourseOwnership(publishedCourse, tutorId);
        doNothing().when(courseManagementService).verifyCourseIsModifiable(publishedCourse);
        when(articleRepository.findByIdAndSectionId(articleId, sectionId)).thenReturn(Optional.of(article));
        when(articleRepository.countBySectionId(sectionId)).thenReturn(2L);
        doNothing().when(articleRepository).delete(article);

        assertDoesNotThrow(() -> courseContentService.deleteArticle(sectionId, articleId, tutorId));
        verify(articleRepository, times(1)).delete(article);
    }

    // AUTHORIZATION TESTS

    @Test
    void authorizeContentView_whenInvalidUserIdFormat_shouldThrowInternalServerError() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getSectionsByCourseForUser(courseId, "invalid-id", studentAuthorities);
        });
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Invalid user ID format"));
    }

    @Test
    void authorizeContentView_whenStudentButNotEnrolled_shouldThrowForbidden() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(courseId, 456L, EnrollmentStatus.ENROLLED)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            courseContentService.getSectionsByCourseForUser(courseId, studentId, studentAuthorities);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("not authorized to view content"));
    }
}