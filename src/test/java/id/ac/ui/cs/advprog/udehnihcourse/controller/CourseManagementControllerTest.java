package id.ac.ui.cs.advprog.udehnihcourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseCreateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.CourseUpdateRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseBrowsingService;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(CourseManagementController.class)
public class CourseManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseManagementService courseManagementService;

    @MockitoBean
    private CourseBrowsingService courseBrowsingService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseCreateRequest createRequest;
    private CourseUpdateRequest updateRequest;
    private CourseResponse courseResponse;
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
                .build();
    }

    @Test
    @WithMockUser(username="tutor-test")
    void createCourse_whenValidRequest_shouldReturnCreated() throws Exception {
        when(courseManagementService.createCourse(any(CourseCreateRequest.class), eq("tutor-test")))
                .thenReturn(courseResponse);

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        // TODO: Add Authorization header simulation
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(courseResponse.getMessage()))
                .andExpect(jsonPath("$.courseId").value(courseResponse.getCourseId()));

        verify(courseManagementService, times(1)).createCourse(any(CourseCreateRequest.class), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test")
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
    @WithMockUser(username="tutor-test")
    void updateCourse_whenValidRequestAndAuthorized_shouldReturnOk() throws Exception {
        when(courseManagementService.updateCourse(anyLong(), any(CourseUpdateRequest.class), eq("tutor-test")))
                .thenReturn(courseResponse);

        mockMvc.perform(put("/api/courses/{courseId}", courseId)
                        .with(csrf())
                        // TODO: Add Authorization header simulation
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(courseResponse.getMessage()))
                .andExpect(jsonPath("$.courseId").value(courseResponse.getCourseId()));

        verify(courseManagementService, times(1)).updateCourse(eq(courseId), any(CourseUpdateRequest.class), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test")
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
    @WithMockUser(username="tutor-test")
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
    @WithMockUser(username="tutor-test")
    void deleteCourse_whenValidRequestAndAuthorized_shouldReturnOk() throws Exception {
        doNothing().when(courseManagementService).deleteCourse(anyLong(), eq("tutor-test"));

        GenericResponse expectedResponse = new GenericResponse("Course deleted successfully");

        mockMvc.perform(delete("/api/courses/{courseId}", courseId)
                                .with(csrf())
                        // TODO: Add Authorization header simulation
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));


        verify(courseManagementService, times(1)).deleteCourse(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test")
    void deleteCourse_whenCourseNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"))
                .when(courseManagementService).deleteCourse(anyLong(), eq("tutor-test"));

        mockMvc.perform(delete("/api/courses/{courseId}", courseId)
                                .with(csrf())
                        // TODO: Add Authorization header simulation
                )
                .andExpect(status().isNotFound());

        verify(courseManagementService, times(1)).deleteCourse(eq(courseId), eq("tutor-test"));
    }

    @Test
    @WithMockUser(username="tutor-test")
    void deleteCourse_whenNotAuthorized_shouldReturnForbidden() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized"))
                .when(courseManagementService).deleteCourse(anyLong(), eq("tutor-test"));

        mockMvc.perform(delete("/api/courses/{courseId}", courseId)
                                .with(csrf())
                        // TODO: Add Authorization header simulation
                )
                .andExpect(status().isForbidden());

        verify(courseManagementService, times(1)).deleteCourse(eq(courseId), eq("tutor-test"));
    }
}
