package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.staff.StaffTutorApplicationUpdateRequestDTO;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.service.TutorRegistrationService;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalApiController {
    private final TutorRegistrationService tutorRegistrationService;

    @PutMapping("/tutor-applications/{applicationId}/status")
    public ResponseEntity<Void> reviewTutorApplicationByStaff(
            @PathVariable Long applicationId,
            @Valid @RequestBody StaffTutorApplicationUpdateRequestDTO request,
            @AuthenticationPrincipal AppUserDetails staffDetails
    ) {

        if (staffDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Staff not authenticated");
        }

        String staffId = String.valueOf(staffDetails.getId());

        log.info("Internal API: Staff {} attempting to update status for application ID {} to {} with feedback: '{}'",
                staffId, applicationId, request.getNewStatus(), request.getFeedback());

        tutorRegistrationService.updateRegistrationStatusByStaff(
                applicationId,
                request.getNewStatus(),
                request.getFeedback(),
                staffId
        );
        return ResponseEntity.ok().build();
    }
}
