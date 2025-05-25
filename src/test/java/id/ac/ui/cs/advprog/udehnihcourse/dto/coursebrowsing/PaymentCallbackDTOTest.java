package id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentCallbackDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        Long enrollmentId = 1L;
        Long studentId = 2L;
        Long courseId = 3L;
        boolean approved = true;
        String message = "Payment successful";

        // Act
        PaymentCallbackDTO dto = new PaymentCallbackDTO(enrollmentId, studentId, courseId, approved, message);

        // Assert
        assertEquals(enrollmentId, dto.getEnrollmentId());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertTrue(dto.isApproved());
        assertEquals(message, dto.getMessage());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long enrollmentId = 1L;
        Long studentId = 2L;
        Long courseId = 3L;
        boolean approved = true;
        String message = "Payment successful";

        // Act
        PaymentCallbackDTO dto = PaymentCallbackDTO.builder()
                .enrollmentId(enrollmentId)
                .studentId(studentId)
                .courseId(courseId)
                .approved(approved)
                .message(message)
                .build();

        // Assert
        assertEquals(enrollmentId, dto.getEnrollmentId());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertTrue(dto.isApproved());
        assertEquals(message, dto.getMessage());
    }

    @Test
    void testSetters() {
        // Arrange
        PaymentCallbackDTO dto = new PaymentCallbackDTO();
        Long enrollmentId = 1L;
        Long studentId = 2L;
        Long courseId = 3L;
        boolean approved = true;
        String message = "Payment successful";

        // Act
        dto.setEnrollmentId(enrollmentId);
        dto.setStudentId(studentId);
        dto.setCourseId(courseId);
        dto.setApproved(approved);
        dto.setMessage(message);

        // Assert
        assertEquals(enrollmentId, dto.getEnrollmentId());
        assertEquals(studentId, dto.getStudentId());
        assertEquals(courseId, dto.getCourseId());
        assertTrue(dto.isApproved());
        assertEquals(message, dto.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        PaymentCallbackDTO dto1 = PaymentCallbackDTO.builder()
                .enrollmentId(1L)
                .studentId(2L)
                .courseId(3L)
                .approved(true)
                .message("Payment successful")
                .build();

        PaymentCallbackDTO dto2 = PaymentCallbackDTO.builder()
                .enrollmentId(1L)
                .studentId(2L)
                .courseId(3L)
                .approved(true)
                .message("Payment successful")
                .build();

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}