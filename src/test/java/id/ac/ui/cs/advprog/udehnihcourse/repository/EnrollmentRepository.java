package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Enrollment;
import id.ac.ui.cs.advprog.udehnihcourse.model.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EnrollmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course createCourse(String title) {
        return Course.builder()
                .title(title)
                .tutorId("tutor1")
                .price(BigDecimal.valueOf(100000))
                .description(title + " course description")
                .tutorId("1")
                .build();
    }

    private Enrollment createEnrollment(String studentId, Course course) {
        return Enrollment.builder()
                .studentId(studentId)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .enrolledAt(LocalDateTime.now())
                .build();
    }

    @Test
    void whenFindByStudentId_thenReturnEnrollments() {
        // Arrange
        Course course1 = createCourse("Java Programming");
        Course course2 = createCourse("Python Programming");
        entityManager.persist(course1);
        entityManager.persist(course2);

        Enrollment enrollment1 = createEnrollment("student1", course1);
        Enrollment enrollment2 = createEnrollment("student1", course2);
        entityManager.persist(enrollment1);
        entityManager.persist(enrollment2);
        entityManager.flush();

        // Act
        List<Enrollment> found = enrollmentRepository.findByStudentId("student1");

        // Assert
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(e -> e.getCourse().getTitle().equals("Java Programming")));
        assertTrue(found.stream().anyMatch(e -> e.getCourse().getTitle().equals("Python Programming")));
    }

    @Test
    void whenFindByStudentIdAndCourseId_thenReturnEnrollment() {
        // Arrange
        Course course = createCourse("Java Programming");
        entityManager.persist(course);

        Enrollment enrollment = createEnrollment("student1", course);
        entityManager.persist(enrollment);
        entityManager.flush();

        // Act
        Optional<Enrollment> found = enrollmentRepository.findByStudentIdAndCourseId("student1", course.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Java Programming", found.get().getCourse().getTitle());
        assertEquals("student1", found.get().getStudentId());
        assertEquals(EnrollmentStatus.ENROLLED, found.get().getStatus());
    }

    @Test
    void whenExistsByStudentIdAndCourseId_thenReturnTrue() {
        // Arrange
        Course course = createCourse("Java Programming");
        entityManager.persist(course);

        Enrollment enrollment = createEnrollment("student1", course);
        entityManager.persist(enrollment);
        entityManager.flush();

        // Act
        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId("student1", course.getId());
        boolean nonExists = enrollmentRepository.existsByStudentIdAndCourseId("student2", course.getId());

        // Assert
        assertTrue(exists);
        assertFalse(nonExists);
    }

    @Test
    void whenFindByNonExistentStudentId_thenReturnEmptyList() {
        // Arrange
        Course course = createCourse("Java Programming");
        entityManager.persist(course);
        entityManager.flush();

        // Act
        List<Enrollment> found = enrollmentRepository.findByStudentId("nonexistent");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    void whenFindByNonExistentStudentIdAndCourseId_thenReturnEmpty() {
        // Arrange
        Course course = createCourse("Java Programming");
        entityManager.persist(course);
        entityManager.flush();

        // Act
        Optional<Enrollment> found = enrollmentRepository.findByStudentIdAndCourseId("nonexistent", course.getId());

        // Assert
        assertTrue(found.isEmpty());
    }
}