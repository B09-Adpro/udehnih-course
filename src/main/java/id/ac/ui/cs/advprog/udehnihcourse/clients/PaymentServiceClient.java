package id.ac.ui.cs.advprog.udehnihcourse.clients;

import id.ac.ui.cs.advprog.udehnihcourse.config.FeignConfig;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.PaymentRequestDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-service",
        url = "${services.payment.baseurl}",
        configuration = FeignConfig.class
)

public interface PaymentServiceClient {
    @PostMapping("/api/payments")
    PaymentResponseDTO createPaymentRequest(@RequestBody PaymentRequestDTO paymentRequest);
}
