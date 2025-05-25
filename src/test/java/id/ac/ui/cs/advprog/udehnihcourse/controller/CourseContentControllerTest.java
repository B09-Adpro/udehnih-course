package id.ac.ui.cs.advprog.udehnihcourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionResponse;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseContentController.class)
@ActiveProfiles("test")
class CourseContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseContentService courseContentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long courseId = 1L;
    private Long sectionId = 10L;
    private Long articleId = 100L;
    private String mockTutorId = "tutor-content-placeholder";
    private String mockStudentId = "123";

    private SectionRequest sectionRequest;
    private SectionResponse sectionResponse;
    private ArticleRequest articleRequest;
    private ArticleResponse articleResponse;

    @BeforeEach
    void setUp() {
        sectionRequest = new SectionRequest();
        sectionRequest.setTitle("New Section");

        sectionResponse = SectionResponse.builder()
                .id(sectionId)
                .title("New Section")
                .courseId(courseId)
                .build();

        articleRequest = new ArticleRequest();
        articleRequest.setTitle("New Article");
        articleRequest.setContent("Content");
        articleRequest.setContentType("TEXT");

        articleResponse = ArticleResponse.builder()
                .id(articleId)
                .title("New Article")
                .content("Content")
                .contentType("TEXT")
                .sectionId(sectionId)
                .build();
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addSection_whenValid_shouldReturnCreated() throws Exception {
        when(courseContentService.addSectionToCourse(eq(courseId), any(SectionRequest.class), eq(mockTutorId)))
                .thenReturn(sectionResponse);

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sectionId))
                .andExpect(jsonPath("$.title").value("New Section"))
                .andExpect(jsonPath("$.courseId").value(courseId))
                .andExpect(header().exists("Location"));

        verify(courseContentService).addSectionToCourse(eq(courseId), any(SectionRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addSection_whenCourseNotFound_shouldReturnNotFound() throws Exception {
        when(courseContentService.addSectionToCourse(eq(courseId), any(SectionRequest.class), eq(mockTutorId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "other-tutor", authorities = {"ROLE_TUTOR"})
    void addSection_whenNotOwner_shouldReturnForbidden() throws Exception {
        when(courseContentService.addSectionToCourse(eq(courseId), any(SectionRequest.class), eq("other-tutor")))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"));

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addSection_whenCoursePendingReview_shouldReturnForbidden() throws Exception {
        when(courseContentService.addSectionToCourse(eq(courseId), any(SectionRequest.class), eq(mockTutorId)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Course cannot be modified while PENDING_REVIEW"));

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student-user", authorities = {"ROLE_STUDENT"})
    void addSection_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isForbidden());

        verify(courseContentService, never()).addSectionToCourse(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addSection_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        SectionRequest invalidRequest = new SectionRequest();
        // Missing title

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseContentService, never()).addSectionToCourse(any(), any(), any());
    }

    @Test
    @WithMockUser(username="student-user", authorities = {"ROLE_STUDENT"})
    void getSections_whenStudent_shouldReturnOk() throws Exception {
        List<SectionResponse> sectionList = Collections.singletonList(sectionResponse);
        when(courseContentService.getSectionsByCourseForUser(eq(courseId), eq("student-user"), any()))
                .thenReturn(sectionList);

        mockMvc.perform(get("/api/courses/{courseId}/sections", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(sectionId))
                .andExpect(jsonPath("$[0].title").value("New Section"));

        verify(courseContentService).getSectionsByCourseForUser(eq(courseId), eq("student-user"), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void getSections_whenTutor_shouldReturnOk() throws Exception {
        List<SectionResponse> sectionList = Collections.singletonList(sectionResponse);
        when(courseContentService.getSectionsByCourseForUser(eq(courseId), eq(mockTutorId), any()))
                .thenReturn(sectionList);

        mockMvc.perform(get("/api/courses/{courseId}/sections", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sectionId));

        verify(courseContentService).getSectionsByCourseForUser(eq(courseId), eq(mockTutorId), any());
    }

    @Test
    @WithMockUser(username="student-user", authorities = {"ROLE_STUDENT"})
    void getSections_whenNotEnrolled_shouldReturnForbidden() throws Exception {
        when(courseContentService.getSectionsByCourseForUser(eq(courseId), eq("student-user"), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view content"));

        mockMvc.perform(get("/api/courses/{courseId}/sections", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getSections_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/sections", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(courseContentService, never()).getSectionsByCourseForUser(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void updateSection_whenValid_shouldReturnOk() throws Exception {
        when(courseContentService.updateSection(eq(courseId), eq(sectionId), any(SectionRequest.class), eq(mockTutorId)))
                .thenReturn(sectionResponse);

        mockMvc.perform(put("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sectionId))
                .andExpect(jsonPath("$.title").value("New Section"));

        verify(courseContentService).updateSection(eq(courseId), eq(sectionId), any(SectionRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void updateSection_whenSectionNotFound_shouldReturnNotFound() throws Exception {
        when(courseContentService.updateSection(eq(courseId), eq(sectionId), any(SectionRequest.class), eq(mockTutorId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));

        mockMvc.perform(put("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "student-user", authorities = {"ROLE_STUDENT"})
    void updateSection_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isForbidden());

        verify(courseContentService, never()).updateSection(any(), any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void deleteSection_whenValid_shouldReturnOk() throws Exception {
        doNothing().when(courseContentService).deleteSection(eq(courseId), eq(sectionId), eq(mockTutorId));

        mockMvc.perform(delete("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Section deleted successfully"));

        verify(courseContentService).deleteSection(eq(courseId), eq(sectionId), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void deleteSection_whenLastSectionInPublishedCourse_shouldReturnBadRequest() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the last section"))
                .when(courseContentService).deleteSection(eq(courseId), eq(sectionId), eq(mockTutorId));

        mockMvc.perform(delete("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "student-user", authorities = {"ROLE_STUDENT"})
    void deleteSection_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(courseContentService, never()).deleteSection(any(), any(), any());
    }

    // ARTICLE TESTS

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addArticle_whenValid_shouldReturnCreated() throws Exception {
        when(courseContentService.addArticleToSection(eq(sectionId), any(ArticleRequest.class), eq(mockTutorId)))
                .thenReturn(articleResponse);

        mockMvc.perform(post("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(articleId))
                .andExpect(jsonPath("$.title").value("New Article"))
                .andExpect(header().exists("Location"));

        verify(courseContentService).addArticleToSection(eq(sectionId), any(ArticleRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addArticle_whenSectionNotFound_shouldReturnNotFound() throws Exception {
        when(courseContentService.addArticleToSection(eq(sectionId), any(ArticleRequest.class), eq(mockTutorId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));

        mockMvc.perform(post("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "other-tutor", authorities = {"ROLE_TUTOR"})
    void addArticle_whenNotOwner_shouldReturnForbidden() throws Exception {
        when(courseContentService.addArticleToSection(eq(sectionId), any(ArticleRequest.class), eq("other-tutor")))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"));

        mockMvc.perform(post("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student-user", authorities = {"ROLE_STUDENT"})
    void addArticle_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isForbidden());

        verify(courseContentService, never()).addArticleToSection(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addArticle_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        ArticleRequest invalidRequest = new ArticleRequest();
        // Missing required fields

        mockMvc.perform(post("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseContentService, never()).addArticleToSection(any(), any(), any());
    }

    @Test
    @WithMockUser(username="student-user", authorities = {"ROLE_STUDENT"})
    void getArticles_whenStudent_shouldReturnOk() throws Exception {
        List<ArticleResponse> articleList = Collections.singletonList(articleResponse);
        when(courseContentService.getArticlesBySectionForUser(eq(sectionId), eq("student-user"), any()))
                .thenReturn(articleList);

        mockMvc.perform(get("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(articleId))
                .andExpect(jsonPath("$[0].title").value("New Article"));

        verify(courseContentService).getArticlesBySectionForUser(eq(sectionId), eq("student-user"), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void getArticles_whenTutor_shouldReturnOk() throws Exception {
        List<ArticleResponse> articleList = Collections.singletonList(articleResponse);
        when(courseContentService.getArticlesBySectionForUser(eq(sectionId), eq(mockTutorId), any()))
                .thenReturn(articleList);

        mockMvc.perform(get("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(articleId));

        verify(courseContentService).getArticlesBySectionForUser(eq(sectionId), eq(mockTutorId), any());
    }

    @Test
    @WithMockUser(username="student-user", authorities = {"ROLE_STUDENT"})
    void getArticles_whenNotEnrolled_shouldReturnForbidden() throws Exception {
        when(courseContentService.getArticlesBySectionForUser(eq(sectionId), eq("student-user"), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view content"));

        mockMvc.perform(get("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void updateArticle_whenValid_shouldReturnOk() throws Exception {
        when(courseContentService.updateArticle(eq(sectionId), eq(articleId), any(ArticleRequest.class), eq(mockTutorId)))
                .thenReturn(articleResponse);

        mockMvc.perform(put("/api/courses/{courseId}/sections/{sectionId}/articles/{articleId}", courseId, sectionId, articleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(articleId))
                .andExpect(jsonPath("$.title").value("New Article"));

        verify(courseContentService).updateArticle(eq(sectionId), eq(articleId), any(ArticleRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void updateArticle_whenArticleNotFound_shouldReturnNotFound() throws Exception {
        when(courseContentService.updateArticle(eq(sectionId), eq(articleId), any(ArticleRequest.class), eq(mockTutorId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        mockMvc.perform(put("/api/courses/{courseId}/sections/{sectionId}/articles/{articleId}", courseId, sectionId, articleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "student-user", authorities = {"ROLE_STUDENT"})
    void updateArticle_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/courses/{courseId}/sections/{sectionId}/articles/{articleId}", courseId, sectionId, articleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isForbidden());

        verify(courseContentService, never()).updateArticle(any(), any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void deleteArticle_whenValid_shouldReturnOk() throws Exception {
        doNothing().when(courseContentService).deleteArticle(eq(sectionId), eq(articleId), eq(mockTutorId));

        mockMvc.perform(delete("/api/courses/{courseId}/sections/{sectionId}/articles/{articleId}", courseId, sectionId, articleId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Article deleted successfully"));

        verify(courseContentService).deleteArticle(eq(sectionId), eq(articleId), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void deleteArticle_whenLastArticleInPublishedCourse_shouldReturnBadRequest() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the last article"))
                .when(courseContentService).deleteArticle(eq(sectionId), eq(articleId), eq(mockTutorId));

        mockMvc.perform(delete("/api/courses/{courseId}/sections/{sectionId}/articles/{articleId}", courseId, sectionId, articleId)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "student-user", authorities = {"ROLE_STUDENT"})
    void deleteArticle_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/courses/{courseId}/sections/{sectionId}/articles/{articleId}", courseId, sectionId, articleId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(courseContentService, never()).deleteArticle(any(), any(), any());
    }

    // VALIDATION TESTS

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addSection_whenTitleTooLong_shouldReturnBadRequest() throws Exception {
        SectionRequest invalidRequest = new SectionRequest();
        invalidRequest.setTitle("a".repeat(256)); // Exceeds max length

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseContentService, never()).addSectionToCourse(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addArticle_whenTitleTooLong_shouldReturnBadRequest() throws Exception {
        ArticleRequest invalidRequest = new ArticleRequest();
        invalidRequest.setTitle("a".repeat(256)); // Exceeds max length
        invalidRequest.setContent("Valid content");
        invalidRequest.setContentType("TEXT");

        mockMvc.perform(post("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseContentService, never()).addArticleToSection(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addArticle_whenContentTypeTooLong_shouldReturnBadRequest() throws Exception {
        ArticleRequest invalidRequest = new ArticleRequest();
        invalidRequest.setTitle("Valid title");
        invalidRequest.setContent("Valid content");
        invalidRequest.setContentType("a".repeat(51)); // Exceeds max length

        mockMvc.perform(post("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseContentService, never()).addArticleToSection(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", authorities = {"ROLE_TUTOR"})
    void addSection_whenMissingCsrfToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        // Missing .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isForbidden());

        verify(courseContentService, never()).addSectionToCourse(any(), any(), any());
    }

    @Test
    void getAllEndpoints_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/sections", courseId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/courses/{courseId}/sections/{sectionId}/articles", courseId, sectionId))
                .andExpect(status().isUnauthorized());

        verify(courseContentService, never()).getSectionsByCourseForUser(any(), any(), any());
        verify(courseContentService, never()).getArticlesBySectionForUser(any(), any(), any());
    }
}