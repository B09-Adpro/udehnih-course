package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.GenericResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.staff.*;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseManagementService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalApiController {

    private final TutorRegistrationService tutorRegistrationService;
    private final CourseManagementService courseManagementService;

    @GetMapping("/tutor-applications")
    public ResponseEntity<StaffTutorApplicationListResponseDTO> getAllTutorApplications(
            @RequestParam(value = "status", required = false) TutorRegistrationStatus statusFilter,
            @AuthenticationPrincipal AppUserDetails staffDetails
    ) {
        log.info("Staff {} fetching tutor applications with status filter: {}",
                staffDetails != null ? staffDetails.getUsername() : "UNKNOWN_STAFF", statusFilter);

        List<StaffTutorApplicationViewDTO> applications = tutorRegistrationService.findApplicationsByStatusForStaff(statusFilter);
        return ResponseEntity.ok(new StaffTutorApplicationListResponseDTO(applications));
    }

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

    @GetMapping("/course-applications")
    public ResponseEntity<StaffCoursePendingReviewListResponseDTO> getAllCoursesPendingReview(
            @AuthenticationPrincipal AppUserDetails staffDetails
    ) {
        log.info("Staff {} fetching courses pending review.",
                staffDetails != null ? staffDetails.getUsername() : "UNKNOWN_STAFF");

        List<StaffCoursePendingReviewViewDTO> courses = courseManagementService.getCoursesPendingReviewForStaff();
        return ResponseEntity.ok(new StaffCoursePendingReviewListResponseDTO(courses));
    }

    @PutMapping("/course-applications/{courseId}/status")
    public ResponseEntity<GenericResponse> reviewCourseByStaff(
            @PathVariable Long courseId,
            @Valid @RequestBody StaffCourseReviewRequestDTO request,
            @AuthenticationPrincipal AppUserDetails staffDetails
    ) {
        String staffId = (staffDetails != null) ? staffDetails.getUsername() : "STAFF_SYSTEM";
        courseManagementService.reviewCourseByStaff(
                courseId,
                request.getNewStatus(),
                request.getFeedback(),
                staffId
        );
        return ResponseEntity.ok(new GenericResponse("Course " + courseId + " review status updated to " + request.getNewStatus()));
    }
}
