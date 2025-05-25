package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.RoleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.RoleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.staff.StaffTutorApplicationViewDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.NotificationType;
import id.ac.ui.cs.advprog.udehnihcourse.model.RoleType;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.repository.TutorRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TutorRegistrationServiceTest {

    @Mock
    private TutorRegistrationRepository tutorRegistrationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private TutorRegistrationService tutorRegistrationService;

    private String studentId;
    private String staffId;
    private TutorApplicationRequest request;
    private TutorRegistration existingPendingApp;
    private TutorRegistration existingAcceptedApp;
    private TutorRegistration existingDeniedApp;
    private TutorRegistration newSavedApp;

    @BeforeEach
    void setUp() {
        studentId = "student-test-1";
        staffId = "staff-test-1";
        request = new TutorApplicationRequest();
        request.setExperience("Exp");
        request.setQualifications("Qual");
        request.setBio("Bio");

        LocalDateTime now = LocalDateTime.now();

        existingPendingApp = new TutorRegistration(studentId, "OldExp", "OldQual", "OldBio");
        existingPendingApp.setId(1L);
        existingPendingApp.setStatus(TutorRegistrationStatus.PENDING);
        existingPendingApp.setSubmittedAt(now.minusDays(1));

        existingAcceptedApp = new TutorRegistration(studentId, "OldExp", "OldQual", "OldBio");
        existingAcceptedApp.setId(2L);
        existingAcceptedApp.setStatus(TutorRegistrationStatus.ACCEPTED);
        existingAcceptedApp.setSubmittedAt(now.minusDays(2));

        existingDeniedApp = new TutorRegistration(studentId, "OldExp", "OldQual", "OldBio");
        existingDeniedApp.setId(3L);
        existingDeniedApp.setStatus(TutorRegistrationStatus.DENIED);
        existingDeniedApp.setSubmittedAt(now.minusDays(3));

        newSavedApp = new TutorRegistration(studentId, request.getExperience(), request.getQualifications(), request.getBio());
        newSavedApp.setId(10L);
        newSavedApp.setStatus(TutorRegistrationStatus.PENDING);
    }

    @Test
    void applyAsTutor_whenNoExistingApplication_shouldSucceed() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.empty());
        when(tutorRegistrationRepository.save(any(TutorRegistration.class))).thenAnswer(invocation -> {
            TutorRegistration appToSave = invocation.getArgument(0);
            appToSave.setId(10L);
            appToSave.setSubmittedAt(LocalDateTime.now());
            return appToSave;
        });

        TutorApplicationResponse response = tutorRegistrationService.applyAsTutor(request, studentId);

        assertNotNull(response);
        assertEquals("Tutor application submitted successfully", response.getMessage());
        assertEquals(10L, response.getApplicationId());
        assertEquals(TutorRegistrationStatus.PENDING, response.getStatus());
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, times(1)).save(any(TutorRegistration.class));
        verify(tutorRegistrationRepository, never()).delete(any(TutorRegistration.class));
    }

    @Test
    void applyAsTutor_whenPendingApplicationExists_shouldThrowConflict() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.of(existingPendingApp));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tutorRegistrationService.applyAsTutor(request, studentId);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Application already exists with status: PENDING"));
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, never()).save(any(TutorRegistration.class));
        verify(tutorRegistrationRepository, never()).delete(any(TutorRegistration.class));
    }

    @Test
    void applyAsTutor_whenAcceptedApplicationExists_shouldThrowConflict() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.of(existingAcceptedApp));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tutorRegistrationService.applyAsTutor(request, studentId);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Application already exists with status: ACCEPTED"));
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, never()).save(any(TutorRegistration.class));
        verify(tutorRegistrationRepository, never()).delete(any(TutorRegistration.class));
    }

    @Test
    void applyAsTutor_whenDeniedApplicationExists_shouldDeleteOldAndSucceed() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.of(existingDeniedApp));
        when(tutorRegistrationRepository.save(any(TutorRegistration.class))).thenAnswer(invocation -> {
            TutorRegistration appToSave = invocation.getArgument(0);
            appToSave.setId(10L);
            appToSave.setSubmittedAt(LocalDateTime.now());
            return appToSave;
        });
        doNothing().when(tutorRegistrationRepository).delete(existingDeniedApp);
        doNothing().when(tutorRegistrationRepository).flush();

        TutorApplicationResponse response = tutorRegistrationService.applyAsTutor(request, studentId);

        assertNotNull(response);
        assertEquals(10L, response.getApplicationId());
        assertEquals(TutorRegistrationStatus.PENDING, response.getStatus());
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, times(1)).delete(existingDeniedApp);
        verify(tutorRegistrationRepository, times(1)).flush();
        verify(tutorRegistrationRepository, times(1)).save(any(TutorRegistration.class));
    }

    @Test
    void checkApplicationStatus_whenApplicationExists_shouldReturnStatusDTO() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.of(existingPendingApp));

        TutorApplicationStatusResponse response = tutorRegistrationService.checkApplicationStatus(studentId);

        assertNotNull(response);
        assertEquals(existingPendingApp.getId(), response.getApplicationId());
        assertEquals(existingPendingApp.getStatus(), response.getStatus());
        assertEquals(existingPendingApp.getSubmittedAt(), response.getSubmittedAt());
        assertEquals(existingPendingApp.getExperience(), response.getExperience());
        assertEquals(existingPendingApp.getQualifications(), response.getQualifications());
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void checkApplicationStatus_whenNoApplication_shouldThrowNotFound() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tutorRegistrationService.checkApplicationStatus(studentId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No application found", exception.getReason());
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void cancelTutorApplication_whenPendingApplicationExists_shouldDelete() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.of(existingPendingApp));
        doNothing().when(tutorRegistrationRepository).delete(existingPendingApp);
        doNothing().when(tutorRegistrationRepository).flush();

        assertDoesNotThrow(() -> tutorRegistrationService.cancelTutorApplication(studentId));

        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, times(1)).delete(existingPendingApp);
        verify(tutorRegistrationRepository, times(1)).flush();
    }

    @Test
    void cancelTutorApplication_whenNoApplication_shouldThrowNotFound() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tutorRegistrationService.cancelTutorApplication(studentId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No application found", exception.getReason());
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, never()).delete(any(TutorRegistration.class));
    }

    @Test
    void cancelTutorApplication_whenApplicationNotPending_shouldThrowBadRequest() {
        when(tutorRegistrationRepository.findByStudentId(studentId)).thenReturn(Optional.of(existingAcceptedApp));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            tutorRegistrationService.cancelTutorApplication(studentId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("No pending application found to cancel", exception.getReason());
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, never()).delete(any(TutorRegistration.class));
    }

    // NEW TESTS FOR STAFF FUNCTIONALITY

    @Test
    void updateRegistrationStatusByStaff_whenAccepted_shouldUpdateStatusAndSendNotificationAndAddRole() {
        Long applicationId = 1L;
        when(tutorRegistrationRepository.findById(applicationId)).thenReturn(Optional.of(existingPendingApp));
        when(tutorRegistrationRepository.save(any(TutorRegistration.class))).thenReturn(existingPendingApp);

        RoleResponse roleResponse = RoleResponse.builder()
                .success(true)
                .message("Role added successfully")
                .build();
        when(authServiceClient.addRoleToUser(any(RoleRequest.class))).thenReturn(roleResponse);

        TutorRegistration result = tutorRegistrationService.updateRegistrationStatusByStaff(
                applicationId, TutorRegistrationStatus.ACCEPTED, "Great application!", staffId);

        assertNotNull(result);
        assertEquals(TutorRegistrationStatus.ACCEPTED, existingPendingApp.getStatus());
        assertNotNull(existingPendingApp.getProcessedAt());

        verify(tutorRegistrationRepository, times(1)).findById(applicationId);
        verify(tutorRegistrationRepository, times(1)).save(existingPendingApp);
        verify(notificationService, times(1)).sendTutorApplicationNotification(
                eq(NotificationType.TUTOR_APPLICATION_ACCEPTED), eq(existingPendingApp), isNull());

        // Use lenient() to allow these stubbing calls for this test
        verify(authServiceClient, times(1)).addRoleToUser(argThat(roleRequest ->
                roleRequest.getUserId().equals(Long.parseLong(studentId)) &&
                        roleRequest.getRoleType().equals(RoleType.TUTOR)
        ));
    }

    @Test
    void updateRegistrationStatusByStaff_whenDenied_shouldUpdateStatusAndSendNotificationWithFeedback() {
        Long applicationId = 1L;
        String feedback = "Need more experience";
        when(tutorRegistrationRepository.findById(applicationId)).thenReturn(Optional.of(existingPendingApp));
        when(tutorRegistrationRepository.save(any(TutorRegistration.class))).thenReturn(existingPendingApp);

        TutorRegistration result = tutorRegistrationService.updateRegistrationStatusByStaff(
                applicationId, TutorRegistrationStatus.DENIED, feedback, staffId);

        assertNotNull(result);
        assertEquals(TutorRegistrationStatus.DENIED, existingPendingApp.getStatus());
        assertNotNull(existingPendingApp.getProcessedAt());

        verify(tutorRegistrationRepository, times(1)).findById(applicationId);
        verify(tutorRegistrationRepository, times(1)).save(existingPendingApp);
        verify(notificationService, times(1)).sendTutorApplicationNotification(
                eq(NotificationType.TUTOR_APPLICATION_REJECTED), eq(existingPendingApp), eq(feedback));
        verify(authServiceClient, never()).addRoleToUser(any(RoleRequest.class));
    }

    @Test
    void updateRegistrationStatusByStaff_whenApplicationNotFound_shouldThrowException() {
        Long applicationId = 999L;
        when(tutorRegistrationRepository.findById(applicationId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tutorRegistrationService.updateRegistrationStatusByStaff(
                    applicationId, TutorRegistrationStatus.ACCEPTED, null, staffId);
        });

        assertTrue(exception.getMessage().contains("Application not found"));
        verify(tutorRegistrationRepository, times(1)).findById(applicationId);
        verify(tutorRegistrationRepository, never()).save(any(TutorRegistration.class));
    }

    @Test
    void updateRegistrationStatusByStaff_whenApplicationNotPending_shouldThrowException() {
        Long applicationId = 2L;
        when(tutorRegistrationRepository.findById(applicationId)).thenReturn(Optional.of(existingAcceptedApp));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            tutorRegistrationService.updateRegistrationStatusByStaff(
                    applicationId, TutorRegistrationStatus.DENIED, "test", staffId);
        });

        assertTrue(exception.getMessage().contains("Application can only be reviewed if its status is PENDING"));
        verify(tutorRegistrationRepository, times(1)).findById(applicationId);
        verify(tutorRegistrationRepository, never()).save(any(TutorRegistration.class));
    }

    @Test
    void updateRegistrationStatusByStaff_whenInvalidStatus_shouldThrowException() {
        Long applicationId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tutorRegistrationService.updateRegistrationStatusByStaff(
                    applicationId, TutorRegistrationStatus.PENDING, null, staffId);
        });

        assertTrue(exception.getMessage().contains("Invalid status update by staff"));
        verify(tutorRegistrationRepository, never()).findById(anyLong());
    }

    @Test
    void updateRegistrationStatusByStaff_whenRoleServiceFails_shouldStillUpdateStatus() {
        Long applicationId = 1L;
        when(tutorRegistrationRepository.findById(applicationId)).thenReturn(Optional.of(existingPendingApp));
        when(tutorRegistrationRepository.save(any(TutorRegistration.class))).thenReturn(existingPendingApp);
        when(authServiceClient.addRoleToUser(any(RoleRequest.class))).thenThrow(new RuntimeException("Auth service error"));

        TutorRegistration result = tutorRegistrationService.updateRegistrationStatusByStaff(
                applicationId, TutorRegistrationStatus.ACCEPTED, null, staffId);

        assertNotNull(result);
        assertEquals(TutorRegistrationStatus.ACCEPTED, existingPendingApp.getStatus());
        verify(tutorRegistrationRepository, times(1)).save(existingPendingApp);
        verify(authServiceClient, times(1)).addRoleToUser(any(RoleRequest.class));
    }

    @Test
    void updateRegistrationStatusByStaff_whenInvalidStudentIdFormat_shouldStillUpdateStatus() {
        Long applicationId = 1L;
        TutorRegistration invalidIdApp = new TutorRegistration("invalid-id", "exp", "qual", "bio");
        invalidIdApp.setId(applicationId);
        invalidIdApp.setStatus(TutorRegistrationStatus.PENDING);

        when(tutorRegistrationRepository.findById(applicationId)).thenReturn(Optional.of(invalidIdApp));
        when(tutorRegistrationRepository.save(any(TutorRegistration.class))).thenReturn(invalidIdApp);

        TutorRegistration result = tutorRegistrationService.updateRegistrationStatusByStaff(
                applicationId, TutorRegistrationStatus.ACCEPTED, null, staffId);

        assertNotNull(result);
        assertEquals(TutorRegistrationStatus.ACCEPTED, invalidIdApp.getStatus());
        verify(tutorRegistrationRepository, times(1)).save(invalidIdApp);
        verify(authServiceClient, never()).addRoleToUser(any(RoleRequest.class)); // Should not be called due to NumberFormatException
    }

    @Test
    void findApplicationsByStatusForStaff_whenStatusFilterProvided_shouldReturnFilteredApplications() {
        List<TutorRegistration> pendingApps = Arrays.asList(existingPendingApp);
        when(tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.PENDING)).thenReturn(pendingApps);

        List<StaffTutorApplicationViewDTO> result = tutorRegistrationService.findApplicationsByStatusForStaff(TutorRegistrationStatus.PENDING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(existingPendingApp.getId(), result.get(0).getApplicationId());
        assertEquals(TutorRegistrationStatus.PENDING, result.get(0).getStatus());
        verify(tutorRegistrationRepository, times(1)).findByStatus(TutorRegistrationStatus.PENDING);
        verify(tutorRegistrationRepository, never()).findAll();
    }

    @Test
    void findApplicationsByStatusForStaff_whenNoStatusFilter_shouldReturnAllApplications() {
        List<TutorRegistration> allApps = Arrays.asList(existingPendingApp, existingAcceptedApp, existingDeniedApp);
        when(tutorRegistrationRepository.findAll()).thenReturn(allApps);

        List<StaffTutorApplicationViewDTO> result = tutorRegistrationService.findApplicationsByStatusForStaff(null);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(tutorRegistrationRepository, times(1)).findAll();
        verify(tutorRegistrationRepository, never()).findByStatus(any());
    }

    @Test
    void findApplicationsByStatusForStaff_whenEmptyResult_shouldReturnEmptyList() {
        when(tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.ACCEPTED)).thenReturn(Arrays.asList());

        List<StaffTutorApplicationViewDTO> result = tutorRegistrationService.findApplicationsByStatusForStaff(TutorRegistrationStatus.ACCEPTED);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tutorRegistrationRepository, times(1)).findByStatus(TutorRegistrationStatus.ACCEPTED);
    }
}