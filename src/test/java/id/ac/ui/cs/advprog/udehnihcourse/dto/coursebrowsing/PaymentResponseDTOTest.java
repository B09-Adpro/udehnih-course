package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentResponseDTOTest {

    @Test
    void testNoArgsConstructor() {
        PaymentResponseDTO response = new PaymentResponseDTO();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getTransactionId());
        assertNull(response.getMessage());
    }

    @Test
    void testAllArgsConstructor() {
        PaymentResponseDTO response = new PaymentResponseDTO(true, "txn-123", "Payment successful");

        assertTrue(response.isSuccess());
        assertEquals("txn-123", response.getTransactionId());
        assertEquals("Payment successful", response.getMessage());
    }

    @Test
    void testBuilder() {
        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .success(true)
                .transactionId("txn-456")
                .message("Payment completed")
                .build();

        assertTrue(response.isSuccess());
        assertEquals("txn-456", response.getTransactionId());
        assertEquals("Payment completed", response.getMessage());
    }

    @Test
    void testGettersAndSetters() {
        PaymentResponseDTO response = new PaymentResponseDTO();

        response.setSuccess(true);
        response.setTransactionId("txn-789");
        response.setMessage("Payment processed");

        assertTrue(response.isSuccess());
        assertEquals("txn-789", response.getTransactionId());
        assertEquals("Payment processed", response.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        PaymentResponseDTO response1 = new PaymentResponseDTO(true, "txn-123", "Success");
        PaymentResponseDTO response2 = new PaymentResponseDTO(true, "txn-123", "Success");
        PaymentResponseDTO response3 = new PaymentResponseDTO(false, "txn-456", "Failed");

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        PaymentResponseDTO response = new PaymentResponseDTO(true, "txn-123", "Payment successful");
        String toString = response.toString();

        assertTrue(toString.contains("success=true"));
        assertTrue(toString.contains("transactionId=txn-123"));
        assertTrue(toString.contains("message=Payment successful"));
    }
}