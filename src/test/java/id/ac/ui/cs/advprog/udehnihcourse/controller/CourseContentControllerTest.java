package id.ac.ui.cs.advprog.udehnihcourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseContentController.class)
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
    @WithMockUser(username = "tutor-content-placeholder", roles = {"TUTOR"})
    void addSection_whenValid_shouldReturnCreated() throws Exception {
        when(courseContentService.addSectionToCourse(eq(courseId), any(SectionRequest.class), eq(mockTutorId)))
                .thenReturn(sectionResponse);

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sectionId))
                .andExpect(jsonPath("$.title").value("New Section"));

        verify(courseContentService).addSectionToCourse(eq(courseId), any(SectionRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username="someuser", roles={"STUDENT"})
    void getSections_shouldReturnOk() throws Exception {
        List<SectionResponse> sectionList = Collections.singletonList(sectionResponse);
        when(courseContentService.getSectionsByCourse(courseId)).thenReturn(sectionList);

        mockMvc.perform(get("/api/courses/{courseId}/sections", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sectionId));

        verify(courseContentService).getSectionsByCourse(courseId);
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", roles = {"TUTOR"})
    void updateSection_whenValid_shouldReturnOk() throws Exception {
        when(courseContentService.updateSection(eq(courseId), eq(sectionId), any(SectionRequest.class), eq(mockTutorId)))
                .thenReturn(sectionResponse);

        mockMvc.perform(put("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sectionId));

        verify(courseContentService).updateSection(eq(courseId), eq(sectionId), any(SectionRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", roles = {"TUTOR"})
    void deleteSection_whenValid_shouldReturnNoContent() throws Exception {
        doNothing().when(courseContentService).deleteSection(eq(courseId), eq(sectionId), eq(mockTutorId));

        mockMvc.perform(delete("/api/courses/{courseId}/sections/{sectionId}", courseId, sectionId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(courseContentService).deleteSection(eq(courseId), eq(sectionId), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", roles = {"TUTOR"})
    void addArticle_whenValid_shouldReturnCreated() throws Exception {
        when(courseContentService.addArticleToSection(eq(sectionId), any(ArticleRequest.class), eq(mockTutorId)))
                .thenReturn(articleResponse);

        mockMvc.perform(post("/api/sections/{sectionId}/articles", sectionId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(articleId));

        verify(courseContentService).addArticleToSection(eq(sectionId), any(ArticleRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username="someuser", roles={"STUDENT"})
    void getArticles_shouldReturnOk() throws Exception {
        List<ArticleResponse> articleList = Collections.singletonList(articleResponse);
        when(courseContentService.getArticlesBySection(sectionId)).thenReturn(articleList);

        mockMvc.perform(get("/api/sections/{sectionId}/articles", sectionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(articleId));

        verify(courseContentService).getArticlesBySection(sectionId);
    }


    @Test
    @WithMockUser(username = "tutor-content-placeholder", roles = {"TUTOR"})
    void updateArticle_whenValid_shouldReturnOk() throws Exception {
        when(courseContentService.updateArticle(eq(sectionId), eq(articleId), any(ArticleRequest.class), eq(mockTutorId)))
                .thenReturn(articleResponse);

        mockMvc.perform(put("/api/sections/{sectionId}/articles/{articleId}", sectionId, articleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(articleId));

        verify(courseContentService).updateArticle(eq(sectionId), eq(articleId), any(ArticleRequest.class), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", roles = {"TUTOR"})
    void deleteArticle_whenValid_shouldReturnNoContent() throws Exception {
        doNothing().when(courseContentService).deleteArticle(eq(sectionId), eq(articleId), eq(mockTutorId));

        mockMvc.perform(delete("/api/sections/{sectionId}/articles/{articleId}", sectionId, articleId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(courseContentService).deleteArticle(eq(sectionId), eq(articleId), eq(mockTutorId));
    }

    @Test
    @WithMockUser(username = "tutor-content-placeholder", roles = {"TUTOR"})
    void addSection_whenCourseNotFound_shouldReturnNotFound() throws Exception {
        when(courseContentService.addSectionToCourse(eq(courseId), any(SectionRequest.class), eq(mockTutorId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        mockMvc.perform(post("/api/courses/{courseId}/sections", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isNotFound());
    }
}