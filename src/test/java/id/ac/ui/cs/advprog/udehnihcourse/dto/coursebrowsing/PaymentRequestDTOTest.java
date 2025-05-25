package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        Long enrollmentId = 5L;
        Long studentId = 1L;
        Long courseId = 2L;
        String courseTitle = "Java Programming";
        String tutorName = "John Doe";
        BigDecimal amount = new BigDecimal("199.99");
        Long timestamp = System.currentTimeMillis();
        String paymentMethod = "BANK_TRANSFER";

        // Act
        PaymentRequestDTO dto = new PaymentRequestDTO(enrollmentId, studentId, courseId,
                courseTitle, tutorName, amount, paymentMethod, timestamp);

        // Assert
        assertEquals(enrollmentId, dto.getEnrollmentId());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long enrollmentId = 5L;
        Long studentId = 1L;
        Long courseId = 2L;
        String courseTitle = "Java Programming";
        String tutorName = "John Doe";
        BigDecimal amount = new BigDecimal("199.99");
        String paymentMethod = "CREDIT_CARD";
        Long timestamp = System.currentTimeMillis();

        // Act
        PaymentRequestDTO dto = PaymentRequestDTO.builder()
                .enrollmentId(enrollmentId)
                .studentId(studentId)
                .courseId(courseId)
                .courseTitle(courseTitle)
                .tutorName(tutorName)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .timestamp(timestamp)
                .build();

        // Assert
        assertEquals(enrollmentId, dto.getEnrollmentId());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testSetters() {
        // Arrange
        PaymentRequestDTO dto = new PaymentRequestDTO();
        Long enrollmentId = 5L;
        Long studentId = 1L;
        Long courseId = 2L;
        String courseTitle = "Java Programming";
        String tutorName = "John Doe";
        BigDecimal amount = new BigDecimal("199.99");
        String paymentMethod = "BANK_TRANSFER";
        Long timestamp = System.currentTimeMillis();

        // Act
        dto.setEnrollmentId(enrollmentId);
        dto.setStudentId(studentId);
        dto.setCourseId(courseId);
        dto.setCourseTitle(courseTitle);
        dto.setTutorName(tutorName);
        dto.setAmount(amount);
        dto.setPaymentMethod(paymentMethod);
        dto.setTimestamp(timestamp);

        // Assert
        assertEquals(enrollmentId, dto.getEnrollmentId());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(amount, dto.getAmount());
        assertEquals(paymentMethod, dto.getPaymentMethod());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        PaymentRequestDTO dto1 = PaymentRequestDTO.builder()
                .enrollmentId(5L)
                .studentId(1L)
                .courseId(2L)
                .courseTitle("Java Programming")
                .tutorName("John Doe")
                .amount(new BigDecimal("199.99"))
                .paymentMethod("CREDIT_CARD")
                .timestamp(1000L)
                .build();

        PaymentRequestDTO dto2 = PaymentRequestDTO.builder()
                .enrollmentId(5L)
                .studentId(1L)
                .courseId(2L)
                .courseTitle("Java Programming")
                .tutorName("John Doe")
                .amount(new BigDecimal("199.99"))
                .paymentMethod("CREDIT_CARD")
                .timestamp(1000L)
                .build();

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}