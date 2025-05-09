package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.tutor.TutorApplicationStatusResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.repository.TutorRegistrationRepository;
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
public class TutorRegistrationService {

    private final TutorRegistrationRepository tutorRegistrationRepository;

    public TutorApplicationResponse applyAsTutor(TutorApplicationRequest request, String studentId) {
        // TODO: Integrate with Spring Security for studentId.

        Optional<TutorRegistration> existing = tutorRegistrationRepository.findByStudentId(studentId);
        if (existing.isPresent()) {
            TutorRegistration reg = existing.get();
            if (reg.getStatus() == TutorRegistrationStatus.PENDING || reg.getStatus() == TutorRegistrationStatus.ACCEPTED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Application already exists with status: " + reg.getStatus());
            } else if (reg.getStatus() == TutorRegistrationStatus.DENIED) {
                tutorRegistrationRepository.delete(reg);
            }
        }

        TutorRegistration newApplication = new TutorRegistration(
                studentId,
                request.getExperience(),
                request.getQualifications(),
                request.getBio()
        );

        TutorRegistration savedApplication = tutorRegistrationRepository.save(newApplication);

        return TutorApplicationResponse.builder()
                .message("Tutor application submitted successfully")
                .applicationId(savedApplication.getId())
                .status(savedApplication.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public TutorApplicationStatusResponse checkApplicationStatus(String studentId) {
        // TODO: Integrate with Spring Security for studentId.

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
        // TODO: Integrate with Spring Security for studentId and ownership check.

        TutorRegistration application = tutorRegistrationRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found"));

        if (application.getStatus() != TutorRegistrationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No pending application found to cancel");
        }

        tutorRegistrationRepository.delete(application);
    }

    public void updateRegistrationStatusByStaff(Long applicationId, TutorRegistrationStatus newStatus, String staffId) {
        // TODO: Implement proper authorization check (ensure caller is Staff).

        if (newStatus != TutorRegistrationStatus.ACCEPTED && newStatus != TutorRegistrationStatus.DENIED) {
            throw new IllegalArgumentException("Invalid status update by staff: " + newStatus);
        }

        TutorRegistration registration = tutorRegistrationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));

        if (registration.getStatus() != TutorRegistrationStatus.PENDING) {
            throw new IllegalStateException("Registration has already been processed or is not pending.");
        }

        registration.setStatus(newStatus);
        registration.setProcessedAt(LocalDateTime.now());

        tutorRegistrationRepository.save(registration);

        // TODO: Sending notif to Student (Observer Pattern via Spring Events or MQ)
        //  kyk applicationEventPublisher.publishEvent(new TutorRegistrationProcessedEvent(this, registration));
    }
}
