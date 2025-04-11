package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TutorRegistrationServiceTest {

    @Mock
    private TutorRegistrationRepository tutorRegistrationRepository;

    @InjectMocks
    private TutorRegistrationService tutorRegistrationService;

    private String studentId;
    private TutorApplicationRequest request;
    private TutorRegistration existingPendingApp;
    private TutorRegistration existingAcceptedApp;
    private TutorRegistration existingDeniedApp;
    private TutorRegistration newSavedApp;

    @BeforeEach
    void setUp() {
        studentId = "student-test-1";
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

        TutorApplicationResponse response = tutorRegistrationService.applyAsTutor(request, studentId);

        assertNotNull(response);
        assertEquals(10L, response.getApplicationId());
        assertEquals(TutorRegistrationStatus.PENDING, response.getStatus());
        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, times(1)).delete(existingDeniedApp);
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

        assertDoesNotThrow(() -> tutorRegistrationService.cancelTutorApplication(studentId));

        verify(tutorRegistrationRepository, times(1)).findByStudentId(studentId);
        verify(tutorRegistrationRepository, times(1)).delete(existingPendingApp);
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

}
