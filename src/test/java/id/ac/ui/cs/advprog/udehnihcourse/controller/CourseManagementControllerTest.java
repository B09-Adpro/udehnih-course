package id.ac.ui.cs.advprog.udehnihcourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.*;
import id.ac.ui.cs.advprog.udehnihcourse.model.CourseStatus;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(CourseManagementController.class)
@ActiveProfiles("test")
public class CourseManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseManagementService courseManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseCreateRequest createRequest;
    private CourseUpdateRequest updateRequest;
    private CourseResponse courseResponse;
    private CourseDetailResponse courseDetailResponse;
    private Long courseId = 1L;

    @BeforeEach
    void setUp() {
        createRequest = new CourseCreateRequest();
        createRequest.setTitle("New Course");
        createRequest.setDescription("Desc");
        createRequest.setCategory("Cat");
        createRequest.setPrice(new BigDecimal("50.00"));

        updateRequest = new CourseUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Desc");
        updateRequest.setPrice(new BigDecimal("75.00"));

        courseResponse = CourseResponse.builder()
                .message("Operation successful")
                .courseId(courseId)
                .status(CourseStatus.DRAFT)
                .build();

        courseDetailResponse = CourseDetailResponse.builder()
                .id(courseId)
                .title("Test Course")
                .description("Test Description")
                .category("Programming")
                .tutorId("tutor-test")
                .price(new BigDecimal("100.00"))
                .status(CourseStatus.DRAFT)
                .enrollmentCount(5)
                .sectionCount(2)
                .articleCount(10)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void createCourse_whenValidRequest_shouldReturnCreated() throws Exception {
        when(courseManagementService.createCourse(any(CourseCreateRequest.class), eq("tutor-test")))
                .thenReturn(courseResponse);

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(courseResponse.getMessage()))
                .andExpect(jsonPath("$.courseId").value(courseResponse.getCourseId()))
                .andExpect(jsonPath("$.status").value(courseResponse.getStatus().toString()))
                .andExpect(header().exists("Location"));

        verify(courseManagementService, times(1)).createCourse(any(CourseCreateRequest.class), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void createCourse_whenNotAuthorized_shouldReturnForbidden() throws Exception {
        when(courseManagementService.createCourse(any(CourseCreateRequest.class), eq("tutor-test")))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an authorized Tutor."));

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(courseManagementService, times(1)).createCourse(any(CourseCreateRequest.class), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void createCourse_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        CourseCreateRequest invalidRequest = new CourseCreateRequest();
        // Missing required fields

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseManagementService, never()).createCourse(any(), any());
    }

    @Test
    @WithMockUser(username="student-test", authorities = {"ROLE_STUDENT"})
    void createCourse_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).createCourse(any(), any());
    }

    @Test
    void createCourse_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());

        verify(courseManagementService, never()).createCourse(any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void getCourseById_whenValidRequest_shouldReturnOk() throws Exception {
        when(courseManagementService.getCourseDetailById(eq(courseId), eq("tutor-test")))
                .thenReturn(courseDetailResponse);

        mockMvc.perform(get("/api/courses/{courseId}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(courseDetailResponse.getId()))
                .andExpect(jsonPath("$.title").value(courseDetailResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(courseDetailResponse.getDescription()))
                .andExpect(jsonPath("$.enrollmentCount").value(courseDetailResponse.getEnrollmentCount()))
                .andExpect(jsonPath("$.sectionCount").value(courseDetailResponse.getSectionCount()))
                .andExpect(jsonPath("$.articleCount").value(courseDetailResponse.getArticleCount()));

        verify(courseManagementService, times(1)).getCourseDetailById(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="other-tutor", authorities = {"ROLE_TUTOR"})
    void getCourseById_whenNotOwner_shouldReturnForbidden() throws Exception {
        when(courseManagementService.getCourseDetailById(eq(courseId), eq("other-tutor")))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"));

        mockMvc.perform(get("/api/courses/{courseId}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(courseManagementService, times(1)).getCourseDetailById(eq(courseId), eq("other-tutor"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void updateCourse_whenValidRequestAndAuthorized_shouldReturnOk() throws Exception {
        when(courseManagementService.updateCourse(anyLong(), any(CourseUpdateRequest.class), eq("tutor-test")))
                .thenReturn(courseResponse);

        mockMvc.perform(put("/api/courses/{courseId}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(courseResponse.getMessage()))
                .andExpect(jsonPath("$.courseId").value(courseResponse.getCourseId()));

        verify(courseManagementService, times(1)).updateCourse(eq(courseId), any(CourseUpdateRequest.class), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void updateCourse_whenCourseNotFound_shouldReturnNotFound() throws Exception {
        when(courseManagementService.updateCourse(anyLong(), any(CourseUpdateRequest.class), eq("tutor-test")))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        mockMvc.perform(put("/api/courses/{courseId}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(courseManagementService, times(1)).updateCourse(eq(courseId), any(CourseUpdateRequest.class), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void updateCourse_whenNotAuthorized_shouldReturnForbidden() throws Exception {
        when(courseManagementService.updateCourse(anyLong(), any(CourseUpdateRequest.class), eq("tutor-test")))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"));

        mockMvc.perform(put("/api/courses/{courseId}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        verify(courseManagementService, times(1)).updateCourse(eq(courseId), any(CourseUpdateRequest.class), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void updateCourse_whenCoursePendingReview_shouldReturnForbidden() throws Exception {
        when(courseManagementService.updateCourse(anyLong(), any(CourseUpdateRequest.class), eq("tutor-test")))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Course cannot be modified while PENDING_REVIEW"));

        mockMvc.perform(put("/api/courses/{courseId}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="student-test", authorities = {"ROLE_STUDENT"})
    void updateCourse_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/courses/{courseId}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).updateCourse(any(), any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void deleteCourse_whenValidRequestAndAuthorized_shouldReturnOk() throws Exception {
        doNothing().when(courseManagementService).deleteCourse(anyLong(), eq("tutor-test"));

        GenericResponse expectedResponse = new GenericResponse("Course deleted successfully");

        mockMvc.perform(delete("/api/courses/{courseId}", courseId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));

        verify(courseManagementService, times(1)).deleteCourse(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void deleteCourse_whenCourseNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"))
                .when(courseManagementService).deleteCourse(anyLong(), eq("tutor-test"));

        mockMvc.perform(delete("/api/courses/{courseId}", courseId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(courseManagementService, times(1)).deleteCourse(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void deleteCourse_whenNotAuthorized_shouldReturnForbidden() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"))
                .when(courseManagementService).deleteCourse(anyLong(), eq("tutor-test"));

        mockMvc.perform(delete("/api/courses/{courseId}", courseId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(courseManagementService, times(1)).deleteCourse(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="student-test", authorities = {"ROLE_STUDENT"})
    void deleteCourse_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/courses/{courseId}", courseId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).deleteCourse(any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void getEnrolledStudents_whenAuthorized_shouldReturnOk() throws Exception {
        List<CourseEnrollmentStudentDTO> students = Arrays.asList(
                CourseEnrollmentStudentDTO.builder()
                        .studentId("123")
                        .studentName("John Doe")
                        .enrolledAt(LocalDateTime.now().minusDays(3))
                        .build(),
                CourseEnrollmentStudentDTO.builder()
                        .studentId("456")
                        .studentName("Jane Smith")
                        .enrolledAt(LocalDateTime.now().minusDays(1))
                        .build()
        );

        when(courseManagementService.getEnrolledStudentsForCourse(eq(courseId), eq("tutor-test")))
                .thenReturn(students);

        mockMvc.perform(get("/api/courses/{courseId}/enrollments", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].studentId").value("123"))
                .andExpect(jsonPath("$[0].studentName").value("John Doe"))
                .andExpect(jsonPath("$[1].studentId").value("456"))
                .andExpect(jsonPath("$[1].studentName").value("Jane Smith"));

        verify(courseManagementService, times(1)).getEnrolledStudentsForCourse(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void getEnrolledStudents_whenNoStudents_shouldReturnEmptyArray() throws Exception {
        when(courseManagementService.getEnrolledStudentsForCourse(eq(courseId), eq("tutor-test")))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/courses/{courseId}/enrollments", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username="other-tutor", authorities = {"ROLE_TUTOR"})
    void getEnrolledStudents_whenNotOwner_shouldReturnForbidden() throws Exception {
        when(courseManagementService.getEnrolledStudentsForCourse(eq(courseId), eq("other-tutor")))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"));

        mockMvc.perform(get("/api/courses/{courseId}/enrollments", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="student-test", authorities = {"ROLE_STUDENT"})
    void getEnrolledStudents_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/enrollments", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).getEnrolledStudentsForCourse(any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void submitCourseForReview_whenValidRequest_shouldReturnOk() throws Exception {
        doNothing().when(courseManagementService).submitCourseForReview(eq(courseId), eq("tutor-test"));

        mockMvc.perform(post("/api/courses/{courseId}/submit-review", courseId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Course with ID " + courseId + " submitted for review successfully."));

        verify(courseManagementService, times(1)).submitCourseForReview(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void submitCourseForReview_whenCourseNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"))
                .when(courseManagementService).submitCourseForReview(eq(courseId), eq("tutor-test"));

        mockMvc.perform(post("/api/courses/{courseId}/submit-review", courseId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void submitCourseForReview_whenCourseNotReady_shouldReturnBadRequest() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course must have at least one section"))
                .when(courseManagementService).submitCourseForReview(eq(courseId), eq("tutor-test"));

        mockMvc.perform(post("/api/courses/{courseId}/submit-review", courseId)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="other-tutor", authorities = {"ROLE_TUTOR"})
    void submitCourseForReview_whenNotOwner_shouldReturnForbidden() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"))
                .when(courseManagementService).submitCourseForReview(eq(courseId), eq("other-tutor"));

        mockMvc.perform(post("/api/courses/{courseId}/submit-review", courseId)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="student-test", authorities = {"ROLE_STUDENT"})
    void submitCourseForReview_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/courses/{courseId}/submit-review", courseId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).submitCourseForReview(any(), any());
    }

    @Test
    void submitCourseForReview_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/courses/{courseId}/submit-review", courseId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(courseManagementService, never()).submitCourseForReview(any(), any());
    }

    // VALIDATION TESTS

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void createCourse_whenTitleTooLong_shouldReturnBadRequest() throws Exception {
        CourseCreateRequest invalidRequest = new CourseCreateRequest();
        invalidRequest.setTitle("a".repeat(256)); // Exceeds max length
        invalidRequest.setDescription("Valid description");
        invalidRequest.setCategory("Valid category");
        invalidRequest.setPrice(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseManagementService, never()).createCourse(any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void createCourse_whenDescriptionTooLong_shouldReturnBadRequest() throws Exception {
        CourseCreateRequest invalidRequest = new CourseCreateRequest();
        invalidRequest.setTitle("Valid title");
        invalidRequest.setDescription("a".repeat(1001)); // Exceeds max length
        invalidRequest.setCategory("Valid category");
        invalidRequest.setPrice(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseManagementService, never()).createCourse(any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void createCourse_whenNegativePrice_shouldReturnBadRequest() throws Exception {
        CourseCreateRequest invalidRequest = new CourseCreateRequest();
        invalidRequest.setTitle("Valid title");
        invalidRequest.setDescription("Valid description");
        invalidRequest.setCategory("Valid category");
        invalidRequest.setPrice(new BigDecimal("-10.00")); // Negative price

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseManagementService, never()).createCourse(any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void updateCourse_whenNegativePrice_shouldReturnBadRequest() throws Exception {
        CourseUpdateRequest invalidRequest = new CourseUpdateRequest();
        invalidRequest.setPrice(new BigDecimal("-50.00")); // Negative price

        mockMvc.perform(put("/api/courses/{courseId}", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseManagementService, never()).updateCourse(any(), any(), any());
    }

    @Test
    @WithMockUser(username="tutor-test", authorities = {"ROLE_TUTOR"})
    void createCourse_whenMissingCsrfToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/courses")
                        // Missing .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).createCourse(any(), any());
    }
}