package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackDTO {
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private boolean approved;
    private String message;
}
