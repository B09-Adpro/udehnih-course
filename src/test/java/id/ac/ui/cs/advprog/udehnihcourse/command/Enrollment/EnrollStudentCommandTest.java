package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrollmentDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollStudentCommandTest {

    @Mock
    private CourseEnrollmentService enrollmentService;

    @Test
    void whenExecute_thenCallsServiceMethod() {
        // Arrange
        Long studentId = 1L;
        Long courseId = 2L;
        String paymentMethod = "credit_card";
        EnrollmentDTO expectedDto = new EnrollmentDTO();

        when(enrollmentService.enrollStudentInCourse(studentId, courseId, paymentMethod))
                .thenReturn(expectedDto);

        EnrollStudentCommand command = new EnrollStudentCommand(
                enrollmentService, studentId, courseId, paymentMethod);

        // Act
        EnrollmentDTO result = command.execute();

        // Assert
        assertEquals(expectedDto, result);
        verify(enrollmentService).enrollStudentInCourse(studentId, courseId, paymentMethod);
    }
}