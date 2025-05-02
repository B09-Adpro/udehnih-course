package id.ac.ui.cs.advprog.udehnihcourse.service;

import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrollmentDTO;
import id.ac.ui.cs.advprog.udehnihcourse.dto.coursebrowsing.EnrolledCourseDTO;
import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Enrollment;
import id.ac.ui.cs.advprog.udehnihcourse.model.EnrollmentStatus;
import id.ac.ui.cs.advprog.udehnihcourse.repository.CourseRepository;
import id.ac.ui.cs.advprog.udehnihcourse.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseEnrollmentServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseEnrollmentService courseEnrollmentService;

    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .id(1L)
                .title("Java Programming")
                .tutorId("tutor1")
                .price(BigDecimal.valueOf(100000))
                .description("Java course")
                .tutorId("1")
                .build();

        enrollment = Enrollment.builder()
                .id(1L)
                .studentId("student1")
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .enrolledAt(LocalDateTime.now())
                .build();
    }

    @Test
    void whenEnrollStudentInCourse_thenSuccess() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student1", 1L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        // Act
        EnrollmentDTO result = courseEnrollmentService.enrollStudentInCourse("student1", 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Successfully enrolled in course", result.getMessage());
        assertEquals("Java Programming", result.getCourseTitle());
        assertEquals("ENROLLED", result.getStatus());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void whenEnrollStudentInNonExistentCourse_thenThrowException() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                courseEnrollmentService.enrollStudentInCourse("student1", 1L)
        );
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void whenEnrollAlreadyEnrolledStudent_thenThrowException() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student1", 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                courseEnrollmentService.enrollStudentInCourse("student1", 1L)
        );
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void whenGetStudentEnrollments_thenReturnList() {
        // Arrange
        Course course2 = Course.builder()
                .id(2L)
                .title("Python Programming")
                .tutorId("tutor2")
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .studentId("student1")
                .course(course2)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        when(enrollmentRepository.findByStudentId("student1"))
                .thenReturn(Arrays.asList(enrollment, enrollment2));

        // Act
        List<EnrolledCourseDTO> results = courseEnrollmentService.getStudentEnrollments("student1");

        // Assert
        assertEquals(2, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
        assertEquals("Python Programming", results.get(1).getTitle());
        assertEquals("Tutor Name", results.get(0).getInstructor());
        verify(enrollmentRepository).findByStudentId("student1");
    }

    @Test
    void whenGetStudentEnrollmentsWithNoEnrollments_thenReturnEmptyList() {
        // Arrange
        when(enrollmentRepository.findByStudentId("student1")).thenReturn(List.of());

        // Act
        List<EnrolledCourseDTO> results = courseEnrollmentService.getStudentEnrollments("student1");

        // Assert
        assertTrue(results.isEmpty());
        verify(enrollmentRepository).findByStudentId("student1");
    }
}