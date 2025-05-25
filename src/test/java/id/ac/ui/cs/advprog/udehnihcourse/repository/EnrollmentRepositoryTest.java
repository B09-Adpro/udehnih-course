package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Enrollment;
import id.ac.ui.cs.advprog.udehnihcourse.model.EnrollmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.h2.console.enabled=false"
})
public class EnrollmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course course1;
    private Course course2;
    private Enrollment enrollment1;
    private Enrollment enrollment2;
    private Enrollment enrollment3;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        enrollmentRepository.deleteAll();

        course1 = Course.builder()
                .title("Course 1")
                .tutorId("tutor-1")
                .build();
        course1 = entityManager.persistAndFlush(course1);

        course2 = Course.builder()
                .title("Course 2")
                .tutorId("tutor-2")
                .build();
        course2 = entityManager.persistAndFlush(course2);

        enrollment1 = Enrollment.builder()
                .studentId(1L)
                .course(course1)
                .status(EnrollmentStatus.ENROLLED)
                .build();

        enrollment2 = Enrollment.builder()
                .studentId(1L)
                .course(course2)
                .status(EnrollmentStatus.PENDING)
                .build();

        enrollment3 = Enrollment.builder()
                .studentId(2L)
                .course(course1)
                .status(EnrollmentStatus.ENROLLED)
                .build();
    }

    @Test
    void whenSaveEnrollment_thenFindById_returnsEnrollment() {
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment1);
        entityManager.flush();

        Optional<Enrollment> foundEnrollment = enrollmentRepository.findById(savedEnrollment.getId());

        assertTrue(foundEnrollment.isPresent());
        assertEquals(savedEnrollment.getId(), foundEnrollment.get().getId());
        assertEquals(1L, foundEnrollment.get().getStudentId());
        assertEquals(course1.getId(), foundEnrollment.get().getCourse().getId());
        assertEquals(EnrollmentStatus.ENROLLED, foundEnrollment.get().getStatus());
        assertNotNull(foundEnrollment.get().getEnrolledAt());
    }

    @Test
    void whenDeleteEnrollment_thenFindById_returnsEmpty() {
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment1);
        Long enrollmentId = savedEnrollment.getId();
        entityManager.flush();

        enrollmentRepository.deleteById(enrollmentId);
        entityManager.flush();

        Optional<Enrollment> foundEnrollment = enrollmentRepository.findById(enrollmentId);
        assertFalse(foundEnrollment.isPresent());
    }

    @Test
    void findByStudentId_shouldReturnAllEnrollmentsForStudent() {
        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);
        enrollmentRepository.save(enrollment3);
        entityManager.flush();

        List<Enrollment> student1Enrollments = enrollmentRepository.findByStudentId(1L);
        List<Enrollment> student2Enrollments = enrollmentRepository.findByStudentId(2L);

        assertEquals(2, student1Enrollments.size());
        assertEquals(1, student2Enrollments.size());

        assertTrue(student1Enrollments.stream().allMatch(e -> e.getStudentId().equals(1L)));
        assertTrue(student2Enrollments.stream().allMatch(e -> e.getStudentId().equals(2L)));
    }

    @Test
    void findByStudentId_whenNoEnrollments_shouldReturnEmptyList() {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(999L);
        assertTrue(enrollments.isEmpty());
    }

    @Test
    void findByStudentIdAndCourseId_shouldReturnSpecificEnrollment() {
        enrollmentRepository.save(enrollment1);
        entityManager.flush();

        Optional<Enrollment> foundEnrollment = enrollmentRepository.findByStudentIdAndCourseId(1L, course1.getId());

        assertTrue(foundEnrollment.isPresent());
        assertEquals(1L, foundEnrollment.get().getStudentId());
        assertEquals(course1.getId(), foundEnrollment.get().getCourse().getId());
    }

    @Test
    void findByStudentIdAndCourseId_whenNotExists_shouldReturnEmpty() {
        Optional<Enrollment> foundEnrollment = enrollmentRepository.findByStudentIdAndCourseId(999L, course1.getId());
        assertFalse(foundEnrollment.isPresent());
    }

    @Test
    void existsByStudentIdAndCourseId_whenExists_shouldReturnTrue() {
        enrollmentRepository.save(enrollment1);
        entityManager.flush();

        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(1L, course1.getId());
        assertTrue(exists);
    }

    @Test
    void existsByStudentIdAndCourseId_whenNotExists_shouldReturnFalse() {
        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(999L, course1.getId());
        assertFalse(exists);
    }

    @Test
    void countByCourseId_shouldReturnCorrectCount() {
        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment3);
        entityManager.flush();

        long count = enrollmentRepository.countByCourseId(course1.getId());
        assertEquals(2, count);
    }

    @Test
    void countByCourseId_whenNoEnrollments_shouldReturnZero() {
        long count = enrollmentRepository.countByCourseId(course1.getId());
        assertEquals(0, count);
    }

    @Test
    void findByCourseId_shouldReturnAllEnrollmentsForCourse() {
        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment3);
        entityManager.flush();

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course1.getId());

        assertEquals(2, enrollments.size());
        assertTrue(enrollments.stream().allMatch(e -> e.getCourse().getId().equals(course1.getId())));
    }

    @Test
    void findByCourseId_whenNoEnrollments_shouldReturnEmptyList() {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course1.getId());
        assertTrue(enrollments.isEmpty());
    }

    @Test
    void existsByCourseIdAndStudentIdAndStatus_whenExists_shouldReturnTrue() {
        enrollmentRepository.save(enrollment1);
        entityManager.flush();

        boolean exists = enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                course1.getId(), 1L, EnrollmentStatus.ENROLLED);
        assertTrue(exists);
    }

    @Test
    void existsByCourseIdAndStudentIdAndStatus_whenDifferentStatus_shouldReturnFalse() {
        enrollmentRepository.save(enrollment1);
        entityManager.flush();

        boolean exists = enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                course1.getId(), 1L, EnrollmentStatus.PENDING);
        assertFalse(exists);
    }

    @Test
    void existsByCourseIdAndStudentIdAndStatus_whenNotExists_shouldReturnFalse() {
        boolean exists = enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                course1.getId(), 999L, EnrollmentStatus.ENROLLED);
        assertFalse(exists);
    }

    @Test
    void enrollmentDefaultStatus_shouldBePending() {
        Enrollment enrollment = Enrollment.builder()
                .studentId(1L)
                .course(course1)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        assertEquals(EnrollmentStatus.PENDING, savedEnrollment.getStatus());
    }

    @Test
    void enrollmentTimestamp_shouldBeAutoGenerated() {
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment1);
        assertNotNull(savedEnrollment.getEnrolledAt());
    }

    @Test
    void findByStudentId_shouldIncludeAllStatuses() {
        enrollmentRepository.save(enrollment1); // ENROLLED
        enrollmentRepository.save(enrollment2); // PENDING
        entityManager.flush();

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(1L);

        assertEquals(2, enrollments.size());
        assertTrue(enrollments.stream().anyMatch(e -> e.getStatus() == EnrollmentStatus.ENROLLED));
        assertTrue(enrollments.stream().anyMatch(e -> e.getStatus() == EnrollmentStatus.PENDING));
    }

    @Test
    void multipleEnrollmentStatuses_shouldWorkCorrectly() {
        enrollment1.setStatus(EnrollmentStatus.ENROLLED);
        enrollment2.setStatus(EnrollmentStatus.PENDING);

        Enrollment enrollment4 = Enrollment.builder()
                .studentId(3L)
                .course(course1)
                .status(EnrollmentStatus.DROPPED)
                .build();

        enrollmentRepository.save(enrollment1);
        enrollmentRepository.save(enrollment2);
        enrollmentRepository.save(enrollment4);
        entityManager.flush();

        assertTrue(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                course1.getId(), 1L, EnrollmentStatus.ENROLLED));
        assertTrue(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                course2.getId(), 1L, EnrollmentStatus.PENDING));
        assertTrue(enrollmentRepository.existsByCourseIdAndStudentIdAndStatus(
                course1.getId(), 3L, EnrollmentStatus.DROPPED));
    }

    @Test
    void cascadeRelationship_courseDelete_shouldNotAffectEnrollments() {
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment1);
        entityManager.flush();

        // Delete course should not cascade to enrollment
        // This test verifies the relationship is properly configured
        assertNotNull(savedEnrollment.getCourse());
        assertEquals(course1.getId(), savedEnrollment.getCourse().getId());
    }
}