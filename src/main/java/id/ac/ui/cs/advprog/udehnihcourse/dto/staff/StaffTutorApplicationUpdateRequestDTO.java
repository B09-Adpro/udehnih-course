package id.ac.ui.cs.advprog.udehnihcourse.dto.staff;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffTutorApplicationUpdateRequestDTO {

    @NotNull(message = "New status is mandatory and cannot be null.")
    private TutorRegistrationStatus newStatus;

    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters.")
    private String feedback;
}
