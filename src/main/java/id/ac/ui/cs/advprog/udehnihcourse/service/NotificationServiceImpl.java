package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihcourse.dto.auth.UserInfoResponse;
import id.ac.ui.cs.advprog.udehnihcourse.model.NotificationType;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final AuthServiceClient authServiceClient;

    @Override
    @Async("userInfoExecutor")
    public void sendTutorApplicationNotification(NotificationType type, TutorRegistration tutorApplication, String feedback) {
        if (tutorApplication == null || tutorApplication.getStudentId() == null || type == null) {
            log.error("Cannot send notification for invalid data. Type: {}, Application: {}", type, tutorApplication);
            return;
        }

        String studentId = tutorApplication.getStudentId();
        String studentEmail = getEmailForUser(studentId);

        if (type != NotificationType.TUTOR_APPLICATION_ACCEPTED && type != NotificationType.TUTOR_APPLICATION_REJECTED) {
            log.warn("Notification not sent for TutorApplication ID: {} as notification type {} is not handled by this method.",
                    tutorApplication.getId(), type);
            return;
        }

        log.info("Attempting to send {} notification for Tutor Application ID: {} for Student ID: {}",
                type, tutorApplication.getId(), studentId);

        String subject = "";
        String body = "";

        if (type == NotificationType.TUTOR_APPLICATION_ACCEPTED) {
            subject = "Congratulations! Your Tutor Application for Udehnih has been Approved";
            body = String.format(
                    "Dear User %s,\n\n" +
                            "We are pleased to inform you that your application to become a Tutor on Udehnih has been approved! " +
                            "You can now log in and start creating your courses.\n\n" +
                            "Welcome to the team!\n\n" +
                            "Regards,\n" +
                            "The Udehnih Team",
                    studentEmail != null ? studentEmail : "User"
            );
        } else { // type == NotificationType.TUTOR_APPLICATION_REJECTED
            subject = "Update on Your Tutor Application for Udehnih";
            body = String.format(
                    "Dear User %s,\n\n" +
                            "Thank you for your interest in becoming a Tutor on Udehnih. " +
                            "After careful review, we regret to inform you that your application was not approved at this time.\n",
                    studentEmail != null ? studentEmail : "User"
            );
            if (feedback != null && !feedback.trim().isEmpty()) {
                body += String.format("\nFeedback from our team: %s\n", feedback);
            }
            body += "\nWe encourage you to address the feedback if provided and re-apply in the future if you wish.\n\n" +
                    "Regards,\n" +
                    "The Udehnih Team";
        }

        try {
            log.info("Simulating email sending to: {} (Student ID: {}) | Subject: {} | Type: {}", studentEmail, studentId, subject, type);
            Thread.sleep(3000);
            log.info("Successfully sent (simulated) {} notification to: {} for application ID: {}", type, studentEmail, tutorApplication.getId());
        } catch (InterruptedException e) {
            log.error("Email sending simulation interrupted for studentId: {} and application ID: {} (Type: {})", studentId, tutorApplication.getId(), type, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Failed to send (simulated) {} notification to: {} for application ID: {}. Error: {}",
                    type, studentEmail, tutorApplication.getId(), e.getMessage());
        }
    }

    private String getEmailForUser(String userId) {
        log.debug("Fetching email for User ID: {}", userId);
        try {
            UserInfoResponse userInfo = authServiceClient.getUserInfoById(userId);
            if (userInfo != null && userInfo.getEmail() != null && !userInfo.getEmail().isEmpty()) {
                log.debug("Email found for User ID {}: {}", userId, userInfo.getEmail());
                return userInfo.getEmail();
            } else {
                log.warn("No email found in UserInfoResponse for User ID {}. UserInfo: {}", userId, userInfo);
            }
        } catch (Exception e) {
            log.error("Failed to fetch email from Auth Service for User ID {}: {}", userId, e.getMessage(), e);
        }
        return null;
    }
}
