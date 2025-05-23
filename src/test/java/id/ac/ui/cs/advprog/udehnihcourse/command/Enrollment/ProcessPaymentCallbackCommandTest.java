package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.PaymentCallbackDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentCallbackCommandTest {

    @Mock
    private CourseEnrollmentService enrollmentService;

    @Test
    void whenExecute_thenCallsServiceMethod() {
        // Arrange
        PaymentCallbackDTO callbackDTO = PaymentCallbackDTO.builder()
                .enrollmentId(1L)
                .studentId(2L)
                .courseId(3L)
                .approved(true)
                .message("Payment successful")
                .build();

        doNothing().when(enrollmentService).processPaymentCallback(callbackDTO);

        ProcessPaymentCallbackCommand command = new ProcessPaymentCallbackCommand(
                enrollmentService, callbackDTO);

        // Act
        Void result = command.execute();

        // Assert
        verify(enrollmentService).processPaymentCallback(callbackDTO);
    }
}