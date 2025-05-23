package id.ac.ui.cs.advprog.udehnihcourse.command.Enrollment;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrolledCourseDTO;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseEnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEnrollmentsCommandTest {

    @Mock
    private CourseEnrollmentService enrollmentService;

    @Test
    void whenExecute_thenCallsServiceMethod() {
        // Arrange
        Long studentId = 1L;
        List<EnrolledCourseDTO> expectedCourses = Arrays.asList(
                new EnrolledCourseDTO(), new EnrolledCourseDTO()
        );

        when(enrollmentService.getStudentEnrollments(studentId))
                .thenReturn(expectedCourses);

        GetEnrollmentsCommand command = new GetEnrollmentsCommand(
                enrollmentService, studentId);

        // Act
        List<EnrolledCourseDTO> result = command.execute();

        // Assert
        assertEquals(expectedCourses, result);
        assertEquals(2, result.size());
        verify(enrollmentService).getStudentEnrollments(studentId);
    }
}