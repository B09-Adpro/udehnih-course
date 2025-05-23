package id.ac.ui.cs.advprog.udehnihcourse.service;
import id.ac.ui.cs.advprog.udehnihcourse.model.NotificationType;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;

public interface NotificationService {
    void sendTutorApplicationNotification(
            NotificationType type,
            TutorRegistration tutorApplication,
            String feedback
    );

}
