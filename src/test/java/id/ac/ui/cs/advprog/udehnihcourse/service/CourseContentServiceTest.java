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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
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

    @InjectMocks
    private CourseContentService courseContentService;

    private Course course;
    private Section section;
    private Article article;
    private String tutorId;
    private Long courseId;
    private Long sectionId;
    private Long articleId;

    @BeforeEach
    void setUp() {
        tutorId = "tutor-content-user";
        courseId = 1L;
        sectionId = 10L;
        articleId = 100L;

        course = Course.builder().id(courseId).title("Test Course").tutorId(tutorId).build();
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
    }

    @Test
    void addSectionToCourse_whenCourseExistsAndTutorOwns_shouldCreateSection() {
        SectionRequest request = new SectionRequest();
        request.setTitle("New Section Title");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseManagementService).verifyCourseOwnership(course, tutorId); // Mock ownership check
        when(sectionRepository.save(any(Section.class))).thenAnswer(invocation -> {
            Section s = invocation.getArgument(0);
            s.setId(sectionId + 1); // Simulate ID generation
            s.setCourse(course); // Ensure course is set
            return s;
        });

        SectionResponse response = courseContentService.addSectionToCourse(courseId, request, tutorId);

        assertNotNull(response);
        assertEquals("New Section Title", response.getTitle());
        assertEquals(courseId, response.getCourseId());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseManagementService, times(1)).verifyCourseOwnership(course, tutorId);
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
    void updateSection_whenValid_shouldUpdateAndReturnSection() {
        SectionRequest request = new SectionRequest();
        request.setTitle("Updated Section Title");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseManagementService).verifyCourseOwnership(course, tutorId);
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId)).thenReturn(Optional.of(section));
        when(sectionRepository.save(any(Section.class))).thenReturn(section); // Assume save updates and returns

        SectionResponse response = courseContentService.updateSection(courseId, sectionId, request, tutorId);

        assertNotNull(response);
        assertEquals("Updated Section Title", section.getTitle()); // Check if the original object was modified
        assertEquals(section.getTitle(), response.getTitle());
    }


    @Test
    void deleteSection_whenValid_shouldDeleteSection() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        doNothing().when(courseManagementService).verifyCourseOwnership(course, tutorId);
        when(sectionRepository.findByIdAndCourseId(sectionId, courseId)).thenReturn(Optional.of(section));
        doNothing().when(sectionRepository).delete(section);

        assertDoesNotThrow(() -> courseContentService.deleteSection(courseId, sectionId, tutorId));
        verify(sectionRepository, times(1)).delete(section);
    }


    @Test
    void addArticleToSection_whenSectionExistsAndTutorOwns_shouldCreateArticle() {
        ArticleRequest request = new ArticleRequest();
        request.setTitle("New Article Title");
        request.setContent("New Content");
        request.setContentType("TEXT");

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section)); // section has course reference
        doNothing().when(courseManagementService).verifyCourseOwnership(section.getCourse(), tutorId);
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
    void updateArticle_whenValid_shouldUpdateAndReturnArticle() {
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Updated Article Title");
        request.setContent("Updated Content");
        request.setContentType("MARKDOWN");

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        doNothing().when(courseManagementService).verifyCourseOwnership(section.getCourse(), tutorId);
        when(articleRepository.findByIdAndSectionId(articleId, sectionId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        ArticleResponse response = courseContentService.updateArticle(sectionId, articleId, request, tutorId);

        assertNotNull(response);
        assertEquals("Updated Article Title", article.getTitle());
        assertEquals("Updated Content", article.getContent());
        assertEquals("MARKDOWN", article.getContentType());
    }

    @Test
    void deleteArticle_whenValid_shouldDeleteArticle() {
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        doNothing().when(courseManagementService).verifyCourseOwnership(section.getCourse(), tutorId);
        when(articleRepository.findByIdAndSectionId(articleId, sectionId)).thenReturn(Optional.of(article));
        doNothing().when(articleRepository).delete(article);

        assertDoesNotThrow(() -> courseContentService.deleteArticle(sectionId, articleId, tutorId));
        verify(articleRepository, times(1)).delete(article);
    }
}