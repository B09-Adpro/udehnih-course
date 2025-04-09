package id.ac.ui.cs.advprog.udehnihcourse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Represents a Student's request to become a Tutor.
 * Design Pattern: Entity (Domain Model) - Represents core domain data.
 */
@Entity
@Table(name = "tutor_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long id;

    @Column(name = "student_id", nullable = false, unique = true)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TutorRegistrationStatus status = TutorRegistrationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false, nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(columnDefinition = "TEXT")
    private String bio;

    public TutorRegistration(String studentId, String experience, String qualifications, String bio) {
        this.studentId = studentId;
        this.experience = experience;
        this.qualifications = qualifications;
        this.bio = bio;
        this.status = TutorRegistrationStatus.PENDING;
    }
}
