package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentRequestDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        Long studentId = 1L;
        Long courseId = 2L;
        BigDecimal amount = new BigDecimal("199.99");
        Long timestamp = System.currentTimeMillis();

        // Act
        PaymentRequestDTO dto = new PaymentRequestDTO(studentId, courseId, amount, timestamp);

        // Assert
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(amount, dto.getAmount());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long studentId = 1L;
        Long courseId = 2L;
        BigDecimal amount = new BigDecimal("199.99");
        Long timestamp = System.currentTimeMillis();

        // Act
        PaymentRequestDTO dto = PaymentRequestDTO.builder()
                .studentId(studentId)
                .courseId(courseId)
                .amount(amount)
                .timestamp(timestamp)
                .build();

        // Assert
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(amount, dto.getAmount());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testSetters() {
        // Arrange
        PaymentRequestDTO dto = new PaymentRequestDTO();
        Long studentId = 1L;
        Long courseId = 2L;
        BigDecimal amount = new BigDecimal("199.99");
        Long timestamp = System.currentTimeMillis();

        // Act
        dto.setStudentId(studentId);
        dto.setCourseId(courseId);
        dto.setAmount(amount);
        dto.setTimestamp(timestamp);

        // Assert
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertEquals(amount, dto.getAmount());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        PaymentRequestDTO dto1 = PaymentRequestDTO.builder()
                .studentId(1L)
                .courseId(2L)
                .amount(new BigDecimal("199.99"))
                .timestamp(1000L)
                .build();

        PaymentRequestDTO dto2 = PaymentRequestDTO.builder()
                .studentId(1L)
                .courseId(2L)
                .amount(new BigDecimal("199.99"))
                .timestamp(1000L)
                .build();

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}