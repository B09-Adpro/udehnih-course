package id.ac.ui.cs.advprog.udehnihcourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.udehnihcourse.config.SecurityConfig;
import id.ac.ui.cs.advprog.udehnihcourse.config.TestConfig;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.course.TutorCourseListItem;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.CourseStatus;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
import id.ac.ui.cs.advprog.udehnihcourse.service.TutorRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(TutorManagementController.class)
@ActiveProfiles("test")
@Import({TestConfig.class, SecurityConfig.class})
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

    private String mockTutorId = "123"; // Changed to match numeric format
    private String mockStudentId = "456"; // Changed to match numeric format

    private AppUserDetails mockTutorUser;
    private AppUserDetails mockStudentUser;

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
                .status(CourseStatus.PUBLISHED)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        mockTutorUser = new AppUserDetails(Long.parseLong(mockTutorId), "tutor@example.com",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TUTOR")));
        mockStudentUser = new AppUserDetails(Long.parseLong(mockStudentId), "student@example.com",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void applyAsTutor_whenValidRequest_shouldReturnCreated() throws Exception {
        when(tutorRegistrationService.applyAsTutor(any(TutorApplicationRequest.class), eq(mockStudentId)))
                .thenReturn(successApplyResponse);

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(successApplyResponse.getMessage()))
                .andExpect(jsonPath("$.applicationId").value(successApplyResponse.getApplicationId()))
                .andExpect(jsonPath("$.status").value(successApplyResponse.getStatus().toString()))
                .andExpect(header().exists("Location"));

        verify(tutorRegistrationService, times(1)).applyAsTutor(any(TutorApplicationRequest.class), eq(mockStudentId));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void applyAsTutor_whenServiceThrowsConflict_shouldReturnConflict() throws Exception {
        when(tutorRegistrationService.applyAsTutor(any(TutorApplicationRequest.class), eq(mockStudentId)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Application already exists"));

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());

        verify(tutorRegistrationService, times(1)).applyAsTutor(any(TutorApplicationRequest.class), eq(mockStudentId));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void applyAsTutor_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
        TutorApplicationRequest invalidRequest = new TutorApplicationRequest();
        // Missing required fields

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, never()).applyAsTutor(any(), any());
    }


    @Test
//    @WithMockUser(username = mockTutorId, authorities = {"ROLE_TUTOR"})
    void applyAsTutor_whenAlreadyTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockTutorUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isForbidden());

        verify(tutorRegistrationService, never()).applyAsTutor(any(), any());
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void checkApplicationStatus_whenApplicationExists_shouldReturnOk() throws Exception {
        when(tutorRegistrationService.checkApplicationStatus(eq(mockStudentId)))
                .thenReturn(statusResponse);

        mockMvc.perform(get("/api/tutors/status")
                        .with(user(mockStudentUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applicationId").value(statusResponse.getApplicationId()))
                .andExpect(jsonPath("$.status").value(statusResponse.getStatus().toString()))
                .andExpect(jsonPath("$.experience").value(statusResponse.getExperience()))
                .andExpect(jsonPath("$.qualifications").value(statusResponse.getQualifications()));

        verify(tutorRegistrationService, times(1)).checkApplicationStatus(eq(mockStudentId));
    }

    @Test
//    @WithMockUser(username = mockTutorId, authorities = {"ROLE_TUTOR"})
    void checkApplicationStatus_whenTutorCheckingStatus_shouldReturnOk() throws Exception {
        when(tutorRegistrationService.checkApplicationStatus(eq(mockTutorId)))
                .thenReturn(statusResponse);

        mockMvc.perform(get("/api/tutors/status")
                        .with(user(mockTutorUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tutorRegistrationService, times(1)).checkApplicationStatus(eq(mockTutorId));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void checkApplicationStatus_whenNoApplication_shouldReturnNotFound() throws Exception {
        when(tutorRegistrationService.checkApplicationStatus(eq(mockStudentId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found"));

        mockMvc.perform(get("/api/tutors/status")
                        .with(user(mockStudentUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tutorRegistrationService, times(1)).checkApplicationStatus(eq(mockStudentId));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void cancelTutorApplication_whenPending_shouldReturnOk() throws Exception {
        doNothing().when(tutorRegistrationService).cancelTutorApplication(eq(mockStudentId));

        GenericResponse expectedResponse = new GenericResponse("Tutor application canceled successfully");

        mockMvc.perform(delete("/api/tutors/apply")
                        .with(csrf())
                .with(user(mockStudentUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()));

        verify(tutorRegistrationService, times(1)).cancelTutorApplication(eq(mockStudentId));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void cancelTutorApplication_whenNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found"))
                .when(tutorRegistrationService).cancelTutorApplication(eq(mockStudentId));

        mockMvc.perform(delete("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser)))
                .andExpect(status().isNotFound());

        verify(tutorRegistrationService, times(1)).cancelTutorApplication(eq(mockStudentId));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void cancelTutorApplication_whenNotPending_shouldReturnBadRequest() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No pending application found to cancel"))
                .when(tutorRegistrationService).cancelTutorApplication(eq(mockStudentId));

        mockMvc.perform(delete("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser)))
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, times(1)).cancelTutorApplication(eq(mockStudentId));
    }

    @Test
//    @WithMockUser(username = mockTutorId, authorities = {"ROLE_TUTOR"})
    void cancelTutorApplication_whenAlreadyTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockTutorUser)))
                .andExpect(status().isForbidden());

        verify(tutorRegistrationService, never()).cancelTutorApplication(any());
    }


    @Test
//    @WithMockUser(username = mockTutorId, authorities = {"ROLE_TUTOR"})
    void getMyCourses_whenAuthorized_shouldReturnOk() throws Exception {
        List<TutorCourseListItem> courseList = Arrays.asList(listItem);
        when(courseManagementService.getCoursesByTutor(eq(mockTutorId))).thenReturn(courseList);

        mockMvc.perform(get("/api/tutors/courses")
                        .with(user(mockTutorUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses[0].id").value(listItem.getId()))
                .andExpect(jsonPath("$.courses[0].title").value(listItem.getTitle()))
                .andExpect(jsonPath("$.courses[0].category").value(listItem.getCategory()))
                .andExpect(jsonPath("$.courses[0].enrollmentCount").value(listItem.getEnrollmentCount()))
                .andExpect(jsonPath("$.courses[0].status").value(listItem.getStatus().toString()));

        verify(courseManagementService, times(1)).getCoursesByTutor(eq(mockTutorId));
    }

    @Test
//    @WithMockUser(username = mockTutorId, authorities = {"ROLE_TUTOR"})
    void getMyCourses_whenNoCourses_shouldReturnEmptyList() throws Exception {
        when(courseManagementService.getCoursesByTutor(eq(mockTutorId))).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tutors/courses")
                        .with(user(mockTutorUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses").isEmpty());

        verify(courseManagementService, times(1)).getCoursesByTutor(eq(mockTutorId));
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void getMyCourses_whenNotTutor_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/tutors/courses")
                        .with(user(mockStudentUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).getCoursesByTutor(any());
    }


    @Test
//    @WithMockUser(username = mockTutorId, authorities = {"ROLE_TUTOR"})
    void getMyCourses_whenServiceThrowsException_shouldReturnError() throws Exception {
        when(courseManagementService.getCoursesByTutor(eq(mockTutorId)))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized tutor"));

        mockMvc.perform(get("/api/tutors/courses")
                        .with(user(mockTutorUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(courseManagementService, times(1)).getCoursesByTutor(eq(mockTutorId));
    }

    // VALIDATION TESTS

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void applyAsTutor_whenExperienceTooLong_shouldReturnBadRequest() throws Exception {
        TutorApplicationRequest invalidRequest = new TutorApplicationRequest();
        invalidRequest.setExperience("a".repeat(2001)); // Exceeds max length
        invalidRequest.setQualifications("Valid qualifications");
        invalidRequest.setBio("Valid bio");

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, never()).applyAsTutor(any(), any());
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void applyAsTutor_whenQualificationsTooLong_shouldReturnBadRequest() throws Exception {
        TutorApplicationRequest invalidRequest = new TutorApplicationRequest();
        invalidRequest.setExperience("Valid experience");
        invalidRequest.setQualifications("a".repeat(1001)); // Exceeds max length
        invalidRequest.setBio("Valid bio");

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, never()).applyAsTutor(any(), any());
    }

    @Test
//    @WithMockUser(username = mockStudentId, authorities = {"ROLE_STUDENT"})
    void applyAsTutor_whenBioTooLong_shouldReturnBadRequest() throws Exception {
        TutorApplicationRequest invalidRequest = new TutorApplicationRequest();
        invalidRequest.setExperience("Valid experience");
        invalidRequest.setQualifications("Valid qualifications");
        invalidRequest.setBio("a".repeat(501));

        mockMvc.perform(post("/api/tutors/apply")
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, never()).applyAsTutor(any(), any());
    }

}