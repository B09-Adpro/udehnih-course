package id.ac.ui.cs.advprog.udehnihcourse.dto.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffCoursePendingReviewListResponseDTO {
    private List<StaffCoursePendingReviewViewDTO> courses;
}