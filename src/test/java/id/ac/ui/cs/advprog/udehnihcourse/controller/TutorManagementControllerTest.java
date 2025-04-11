package id.ac.ui.cs.advprog.udehnihcourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import id.ac.ui.cs.advprog.udehnihcourse.service.TutorRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(TutorManagementController.class)
public class TutorManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TutorRegistrationService tutorRegistrationService;

    @MockitoBean
    private CourseManagementService courseManagementService;

    @Autowired
    private ObjectMapper objectMapper;


    private TutorCourseListItem listItem;

    private TutorApplicationRequest validRequest;
    private TutorApplicationResponse successApplyResponse;
    private TutorApplicationStatusResponse statusResponse;

    @BeforeEach
    void setUp() {
        validRequest = new TutorApplicationRequest();
        validRequest.setExperience("Exp");
        validRequest.setQualifications("Qual");
        validRequest.setBio("Bio");

        successApplyResponse = TutorApplicationResponse.builder()
                .message("Tutor application submitted successfully")
                .applicationId(1L)
                .status(TutorRegistrationStatus.PENDING)
                .build();

        statusResponse = TutorApplicationStatusResponse.builder()
                .applicationId(1L)
                .status(TutorRegistrationStatus.PENDING)
                .submittedAt(LocalDateTime.now().minusDays(1))
                .experience("Exp")
                .qualifications("Qual")
                .build();

        listItem = TutorCourseListItem.builder()
                .id(1L)
                .title("My Course")
                .category("Test Cat")
                .price(new BigDecimal("100.00"))
                .enrollmentCount(10)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }


    @Test
    @WithMockUser(username = "student-test")
    void applyAsTutor_whenValidRequest_shouldReturnCreated() throws Exception {
        when(tutorRegistrationService.applyAsTutor(any(TutorApplicationRequest.class), eq("student-test")))
                .thenReturn(successApplyResponse);

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(successApplyResponse.getMessage()))
                .andExpect(jsonPath("$.applicationId").value(successApplyResponse.getApplicationId()))
                .andExpect(jsonPath("$.status").value(successApplyResponse.getStatus().toString()));

        verify(tutorRegistrationService, times(1)).applyAsTutor(any(TutorApplicationRequest.class), eq("student-test"));
    }

    @Test
    @WithMockUser(username = "student-test")
    void applyAsTutor_whenServiceThrowsConflict_shouldReturnConflict() throws Exception {
        when(tutorRegistrationService.applyAsTutor(any(TutorApplicationRequest.class), eq("student-test")))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Application already exists"));

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());

        verify(tutorRegistrationService, times(1)).applyAsTutor(any(TutorApplicationRequest.class), eq("student-test"));
    }

    @Test
    @WithMockUser(username = "student-test")
    void checkApplicationStatus_whenApplicationExists_shouldReturnOk() throws Exception {
        when(tutorRegistrationService.checkApplicationStatus(anyString()))
                .thenReturn(statusResponse);

        mockMvc.perform(get("/api/tutors/status")
                        // TODO: Add Authorization header simulation
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applicationId").value(statusResponse.getApplicationId()))
                .andExpect(jsonPath("$.status").value(statusResponse.getStatus().toString()))
                .andExpect(jsonPath("$.experience").value(statusResponse.getExperience()));

        verify(tutorRegistrationService, times(1)).checkApplicationStatus(anyString());
    }

    @Test
    @WithMockUser(username = "student-test")
    void checkApplicationStatus_whenNoApplication_shouldReturnNotFound() throws Exception {
        when(tutorRegistrationService.checkApplicationStatus(anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found"));

        mockMvc.perform(get("/api/tutors/status")
                        // TODO: Add Authorization header simulation
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tutorRegistrationService, times(1)).checkApplicationStatus(anyString());
    }

    @Test
    @WithMockUser(username = "student-test")
    void cancelTutorApplication_whenPending_shouldReturnOk() throws Exception {
        doNothing().when(tutorRegistrationService).cancelTutorApplication(eq("student-test"));

        GenericResponse expectedResponse = new GenericResponse("Tutor application canceled successfully");

        mockMvc.perform(delete("/api/tutors/apply")
                                .with(csrf())
                        // TODO: Add Authorization header simulation
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));

        verify(tutorRegistrationService, times(1)).cancelTutorApplication(eq("student-test"));
    }

    @Test
    @WithMockUser(username = "student-test")
    void cancelTutorApplication_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found"))
                .when(tutorRegistrationService).cancelTutorApplication(eq("student-test"));

        mockMvc.perform(delete("/api/tutors/apply")
                                .with(csrf())
                        // TODO: Add Authorization header simulation
                )
                .andExpect(status().isNotFound());

        verify(tutorRegistrationService, times(1)).cancelTutorApplication(eq("student-test"));
    }

    @Test
    @WithMockUser(username = "student-test")
    void cancelTutorApplication_whenNotPending_shouldReturnBadRequest() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No pending application found to cancel"))
                .when(tutorRegistrationService).cancelTutorApplication(eq("student-test"));

        mockMvc.perform(delete("/api/tutors/apply")
                                .with(csrf())
                        // TODO: Add Authorization header simulation
                )
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, times(1)).cancelTutorApplication(eq("student-test"));
    }

    @Test
    @WithMockUser(username = "tutor-test")
    void getMyCourses_whenAuthorized_shouldReturnOk() throws Exception {
        List<TutorCourseListItem> courseList = Arrays.asList(listItem);
        when(courseManagementService.getCoursesByTutor(eq("tutor-test"))).thenReturn(courseList);

        mockMvc.perform(get("/api/tutors/courses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses[0].id").value(listItem.getId()))
                .andExpect(jsonPath("$.courses[0].title").value(listItem.getTitle()))
                .andExpect(jsonPath("$.courses[0].enrollmentCount").value(listItem.getEnrollmentCount()));

        verify(courseManagementService, times(1)).getCoursesByTutor(eq("tutor-test"));

    }
}
