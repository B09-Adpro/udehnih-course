package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.RoleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.RoleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.NotificationType;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.RoleType;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.repository.TutorRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class handling business logic for Tutor Applications.
 * Design Pattern: Service Layer
 * Design Pattern: Dependency Injection
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TutorRegistrationService {

    private final TutorRegistrationRepository tutorRegistrationRepository;
    private final NotificationService notificationService;
    private final AuthServiceClient authServiceClient;

    public TutorApplicationResponse applyAsTutor(TutorApplicationRequest request, String studentId) {
        log.info("Attempting to apply as tutor for studentId: {}", studentId);
        Optional<TutorRegistration> existing = tutorRegistrationRepository.findByStudentId(studentId);
        if (existing.isPresent()) {
            TutorRegistration reg = existing.get();

            log.info("Found existing application for studentId: {} with status: {}", studentId, reg.getStatus());

            if (reg.getStatus() == TutorRegistrationStatus.PENDING || reg.getStatus() == TutorRegistrationStatus.ACCEPTED) {
                log.warn("Student {} already has an active or pending application. Throwing conflict.", studentId);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Application already exists with status: " + reg.getStatus());
            } else if (reg.getStatus() == TutorRegistrationStatus.DENIED) {
                log.info("Deleting previous DENIED application for studentId: {}", studentId);
                tutorRegistrationRepository.delete(reg);
                tutorRegistrationRepository.flush();
                log.info("Flushed context after deleting DENIED application for studentId: {}", studentId);
            }
        } else {
            log.info("No existing application found for studentId: {}. Proceeding with new application.", studentId);
        }

        TutorRegistration newApplication = new TutorRegistration(
                studentId,
                request.getExperience(),
                request.getQualifications(),
                request.getBio()
        );

        log.info("Saving new application for studentId: {}", studentId);
        TutorRegistration savedApplication = tutorRegistrationRepository.save(newApplication);
        log.info("Successfully saved new application ID: {} for studentId: {}", savedApplication.getId(), studentId);

        return TutorApplicationResponse.builder()
                .message("Tutor application submitted successfully")
                .applicationId(savedApplication.getId())
                .status(savedApplication.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public TutorApplicationStatusResponse checkApplicationStatus(String studentId) {

        TutorRegistration application = tutorRegistrationRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found"));

        return TutorApplicationStatusResponse.builder()
                .applicationId(application.getId())
                .status(application.getStatus())
                .submittedAt(application.getSubmittedAt())
                .experience(application.getExperience())
                .qualifications(application.getQualifications())
                .build();
    }

    public void cancelTutorApplication(String studentId) {

        TutorRegistration application = tutorRegistrationRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found"));

        if (application.getStatus() != TutorRegistrationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No pending application found to cancel");
        }

        tutorRegistrationRepository.delete(application);
        tutorRegistrationRepository.flush();
    }

    public TutorRegistration updateRegistrationStatusByStaff(
            Long applicationId,
            TutorRegistrationStatus newStatus,
            String feedbackFromDashboard,
            String staffId
    ) {
        log.info("Staff {} attempting to update application ID {} to status {} with feedback: '{}'",
                staffId, applicationId, newStatus, feedbackFromDashboard);

        if (newStatus != TutorRegistrationStatus.ACCEPTED && newStatus != TutorRegistrationStatus.DENIED) {
            log.error("Invalid status update by staff: {}. Must be ACCEPTED or DENIED.", newStatus);
            throw new IllegalArgumentException("Invalid status update by staff: " + newStatus);
        }

        TutorRegistration registration = tutorRegistrationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    log.error("Application not found for ID: {}", applicationId);
                    return new IllegalArgumentException("Application not found: " + applicationId);
                });

        if (registration.getStatus() != TutorRegistrationStatus.PENDING) {
            log.warn("Attempt to review application ID {} which is not PENDING. Current status: {}",
                    applicationId, registration.getStatus());
            throw new IllegalStateException("Application can only be reviewed if its status is PENDING. Current status: " + registration.getStatus());
        }

        registration.setStatus(newStatus);
        registration.setProcessedAt(LocalDateTime.now());

        TutorRegistration savedRegistration = tutorRegistrationRepository.save(registration);
        log.info("Tutor application ID {} status updated to {} by Staff {}", applicationId, newStatus, staffId);

        NotificationType notificationType;
        String feedbackForNotification = null;

        if (newStatus == TutorRegistrationStatus.ACCEPTED) {
            notificationType = NotificationType.TUTOR_APPLICATION_ACCEPTED;
        } else { // newStatus == TutorRegistrationStatus.DENIED
            notificationType = NotificationType.TUTOR_APPLICATION_REJECTED;
            feedbackForNotification = feedbackFromDashboard;
        }

        notificationService.sendTutorApplicationNotification(notificationType, savedRegistration, feedbackForNotification);

        if (newStatus == TutorRegistrationStatus.ACCEPTED) {
            try {
                Long userIdToUpdate = Long.parseLong(savedRegistration.getStudentId());
                RoleRequest roleRequest = RoleRequest.builder()
                        .userId(userIdToUpdate)
                        .roleType(RoleType.TUTOR)
                        .build();

                log.info("Attempting to add TUTOR role to userId: {} via Auth Service", userIdToUpdate);
                RoleResponse roleResponse = authServiceClient.addRoleToUser(roleRequest);

                if (roleResponse != null && roleResponse.isSuccess()) {
                    log.info("Successfully added TUTOR role to userId: {}. Message: {}", userIdToUpdate, roleResponse.getMessage());
                } else {
                    log.warn("Failed to add TUTOR role to userId: {}. Response: {}", userIdToUpdate, roleResponse);
                }
            } catch (NumberFormatException e) {
                log.error("Failed to parse studentId '{}' to Long for role update.", savedRegistration.getStudentId(), e);
            } catch (Exception e) {
                log.error("Error calling Auth Service to add TUTOR role for userId {}: {}", savedRegistration.getStudentId(), e.getMessage(), e);
            }
        }

        return savedRegistration;
    }

}
