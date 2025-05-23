package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long studentId;
    private Long courseId;
    private BigDecimal amount;
    private String paymentMethod;
    private Long timestamp;
}
