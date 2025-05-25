package id.ac.ui.cs.advprog.udehnihcourse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.udehnihcourse.config.SecurityConfig;
import id.ac.ui.cs.advprog.udehnihcourse.config.TestConfig;
import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.staff.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalApiController.class)
@ActiveProfiles("test")
@Import({TestConfig.class, SecurityConfig.class})
public class InternalApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TutorRegistrationService tutorRegistrationService;

    @MockitoBean
    private CourseManagementService courseManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private StaffTutorApplicationViewDTO tutorApplicationDTO;
    private StaffCoursePendingReviewViewDTO coursePendingReviewDTO;
    private StaffTutorApplicationUpdateRequestDTO tutorUpdateRequest;
    private StaffCourseReviewRequestDTO courseReviewRequest;

    private String mockStaffId = "123"; // Changed to match numeric format
    private String mockStudentId = "456"; // Changed to match numeric format

    private AppUserDetails mockStaffUser;
    private AppUserDetails mockStudentUser;

    @BeforeEach
    void setUp() {
        tutorApplicationDTO = StaffTutorApplicationViewDTO.builder()
                .applicationId(1L)
                .studentId("student-123")
                .experience("5 years teaching")
                .qualifications("PhD in Computer Science")
                .bio("Passionate educator")
                .status(TutorRegistrationStatus.PENDING)
                .submittedAt(LocalDateTime.now().minusDays(2))
                .build();

        coursePendingReviewDTO = StaffCoursePendingReviewViewDTO.builder()
                .courseId(1L)
                .title("Test Course")
                .category("Programming")
                .price(new BigDecimal("100.00"))
                .tutorId("tutor-123")
                .status(CourseStatus.PENDING_REVIEW)
                .sectionCount(2)
                .articleCount(5)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        tutorUpdateRequest = new StaffTutorApplicationUpdateRequestDTO();
        tutorUpdateRequest.setNewStatus(TutorRegistrationStatus.ACCEPTED);
        tutorUpdateRequest.setFeedback("Great application!");

        courseReviewRequest = new StaffCourseReviewRequestDTO();
        courseReviewRequest.setNewStatus(CourseStatus.PUBLISHED);
        courseReviewRequest.setFeedback("Course looks good!");

        mockStaffUser = new AppUserDetails(Long.parseLong(mockStaffId), "staff@example.com",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STAFF")));
        mockStudentUser = new AppUserDetails(Long.parseLong(mockStudentId), "student@example.com",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));
    }


    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void getAllTutorApplications_whenNoFilter_shouldReturnAllApplications() throws Exception {
        List<StaffTutorApplicationViewDTO> applications = Arrays.asList(tutorApplicationDTO);
        when(tutorRegistrationService.findApplicationsByStatusForStaff(null))
                .thenReturn(applications);

        mockMvc.perform(get("/api/internal/tutor-applications")
                        .with(user(mockStaffUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applications").isArray())
                .andExpect(jsonPath("$.applications[0].applicationId").value(1L))
                .andExpect(jsonPath("$.applications[0].studentId").value("student-123"))
                .andExpect(jsonPath("$.applications[0].status").value("PENDING"));

        verify(tutorRegistrationService, times(1)).findApplicationsByStatusForStaff(null);
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void getAllTutorApplications_whenStatusFilter_shouldReturnFilteredApplications() throws Exception {
        List<StaffTutorApplicationViewDTO> applications = Arrays.asList(tutorApplicationDTO);
        when(tutorRegistrationService.findApplicationsByStatusForStaff(TutorRegistrationStatus.PENDING))
                .thenReturn(applications);

        mockMvc.perform(get("/api/internal/tutor-applications")
                        .with(user(mockStaffUser))
                        .param("status", "PENDING")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applications").isArray())
                .andExpect(jsonPath("$.applications[0].status").value("PENDING"));

        verify(tutorRegistrationService, times(1)).findApplicationsByStatusForStaff(TutorRegistrationStatus.PENDING);
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void getAllTutorApplications_whenEmptyResult_shouldReturnEmptyList() throws Exception {
        when(tutorRegistrationService.findApplicationsByStatusForStaff(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/internal/tutor-applications")
                        .with(user(mockStaffUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applications").isArray())
                .andExpect(jsonPath("$.applications").isEmpty());
    }


    @Test
//    @WithMockUser(username = "user@example.com", roles = {"STUDENT"})
    void reviewTutorApplicationByStaff_whenNotStaff_shouldReturnForbidden() throws Exception {
        Long applicationId = 1L;

        mockMvc.perform(put("/api/internal/tutor-applications/{applicationId}/status", applicationId)
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tutorUpdateRequest)))
                .andExpect(status().isForbidden());

        verify(tutorRegistrationService, never()).updateRegistrationStatusByStaff(any(), any(), any(), any());
    }


    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void getAllCoursesPendingReview_shouldReturnPendingCourses() throws Exception {
        List<StaffCoursePendingReviewViewDTO> courses = Arrays.asList(coursePendingReviewDTO);
        when(courseManagementService.getCoursesPendingReviewForStaff()).thenReturn(courses);

        mockMvc.perform(get("/api/internal/course-applications")
                        .with(user(mockStaffUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses[0].courseId").value(1L))
                .andExpect(jsonPath("$.courses[0].title").value("Test Course"))
                .andExpect(jsonPath("$.courses[0].status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$.courses[0].sectionCount").value(2))
                .andExpect(jsonPath("$.courses[0].articleCount").value(5));

        verify(courseManagementService, times(1)).getCoursesPendingReviewForStaff();
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void getAllCoursesPendingReview_whenEmptyResult_shouldReturnEmptyList() throws Exception {
        when(courseManagementService.getCoursesPendingReviewForStaff())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/internal/course-applications")
                        .with(user(mockStaffUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses").isArray())
                .andExpect(jsonPath("$.courses").isEmpty());
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewCourseByStaff_whenValidApproval_shouldReturnOk() throws Exception {
        Long courseId = 1L;
        when(courseManagementService.reviewCourseByStaff(
                eq(courseId), eq(CourseStatus.PUBLISHED), eq("Course looks good!"), anyString()))
                .thenReturn(null);

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReviewRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Course 1 review status updated to PUBLISHED"));

        verify(courseManagementService, times(1)).reviewCourseByStaff(
                eq(courseId), eq(CourseStatus.PUBLISHED), eq("Course looks good!"), anyString());
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewCourseByStaff_whenValidRejection_shouldReturnOk() throws Exception {
        Long courseId = 1L;
        StaffCourseReviewRequestDTO rejectRequest = new StaffCourseReviewRequestDTO();
        rejectRequest.setNewStatus(CourseStatus.REJECTED);
        rejectRequest.setFeedback("Needs more content");

        when(courseManagementService.reviewCourseByStaff(
                eq(courseId), eq(CourseStatus.REJECTED), eq("Needs more content"), anyString()))
                .thenReturn(null);

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Course 1 review status updated to REJECTED"));

        verify(courseManagementService, times(1)).reviewCourseByStaff(
                eq(courseId), eq(CourseStatus.REJECTED), eq("Needs more content"), anyString());
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewCourseByStaff_whenCourseNotFound_shouldReturnNotFound() throws Exception {
        Long courseId = 999L;
        when(courseManagementService.reviewCourseByStaff(any(), any(), any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReviewRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewCourseByStaff_whenCourseNotPendingReview_shouldReturnBadRequest() throws Exception {
        Long courseId = 1L;
        when(courseManagementService.reviewCourseByStaff(any(), any(), any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Course can only be reviewed if it's in PENDING_REVIEW status"));

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReviewRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reviewCourseByStaff_whenNotAuthenticated_shouldReturnForbidden() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReviewRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden()); // Harapkan 403

        verify(courseManagementService, never()).reviewCourseByStaff(any(), any(), any(), any());
    }

    @Test
//    @WithMockUser(username = "user@example.com", roles = {"STUDENT"})
    void reviewCourseByStaff_whenNotStaff_shouldReturnForbidden() throws Exception {
        Long courseId = 1L;

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .with(user(mockStudentUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReviewRequest)))
                .andExpect(status().isForbidden());

        verify(courseManagementService, never()).reviewCourseByStaff(any(), any(), any(), any());
    }


    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewTutorApplicationByStaff_whenMissingStatus_shouldReturnBadRequest() throws Exception {
        Long applicationId = 1L;
        StaffTutorApplicationUpdateRequestDTO invalidRequest = new StaffTutorApplicationUpdateRequestDTO();
        invalidRequest.setFeedback("Some feedback");
        // Missing newStatus

        mockMvc.perform(put("/api/internal/tutor-applications/{applicationId}/status", applicationId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, never()).updateRegistrationStatusByStaff(any(), any(), any(), any());
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewTutorApplicationByStaff_whenFeedbackTooLong_shouldReturnBadRequest() throws Exception {
        Long applicationId = 1L;
        StaffTutorApplicationUpdateRequestDTO invalidRequest = new StaffTutorApplicationUpdateRequestDTO();
        invalidRequest.setNewStatus(TutorRegistrationStatus.DENIED);
        invalidRequest.setFeedback("a".repeat(1001)); // Exceeds 1000 character limit

        mockMvc.perform(put("/api/internal/tutor-applications/{applicationId}/status", applicationId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(tutorRegistrationService, never()).updateRegistrationStatusByStaff(any(), any(), any(), any());
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewCourseByStaff_whenMissingStatus_shouldReturnBadRequest() throws Exception {
        Long courseId = 1L;
        StaffCourseReviewRequestDTO invalidRequest = new StaffCourseReviewRequestDTO();
        invalidRequest.setFeedback("Some feedback");
        // Missing newStatus

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseManagementService, never()).reviewCourseByStaff(any(), any(), any(), any());
    }

    @Test
//    @WithMockUser(username = "staff@example.com", roles = {"STAFF"})
    void reviewCourseByStaff_whenFeedbackTooLong_shouldReturnBadRequest() throws Exception {
        Long courseId = 1L;
        StaffCourseReviewRequestDTO invalidRequest = new StaffCourseReviewRequestDTO();
        invalidRequest.setNewStatus(CourseStatus.REJECTED);
        invalidRequest.setFeedback("a".repeat(1001)); // Exceeds 1000 character limit

        mockMvc.perform(put("/api/internal/course-applications/{courseId}/status", courseId)
                        .with(csrf())
                        .with(user(mockStaffUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(courseManagementService, never()).reviewCourseByStaff(any(), any(), any(), any());
    }
}