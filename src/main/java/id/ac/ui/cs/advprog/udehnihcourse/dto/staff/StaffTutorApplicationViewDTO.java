package id.ac.ui.cs.advprog.udehnihcourse.dto.staff;

import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffTutorApplicationViewDTO {
    private Long applicationId;
    private String studentId;
    private String experience;
    private String qualifications;
    private String bio;
    private TutorRegistrationStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime processedAt;

    public static StaffTutorApplicationViewDTO fromEntity(TutorRegistration entity) {
        return StaffTutorApplicationViewDTO.builder()
                .applicationId(entity.getId())
                .studentId(entity.getStudentId())
                .experience(entity.getExperience())
                .qualifications(entity.getQualifications())
                .bio(entity.getBio())
                .status(entity.getStatus())
                .submittedAt(entity.getSubmittedAt())
                .processedAt(entity.getProcessedAt())
                .build();
    }
}