package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Enrollment;
import id.ac.ui.cs.advprog.udehnihcourse.model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    long countByCourseId(Long courseId);
    List<Enrollment> findByCourseId(Long courseId);
    boolean existsByCourseIdAndStudentIdAndStatus(Long courseId, Long studentId, EnrollmentStatus status);
    boolean existsByStudentIdAndCourseIdAndStatusEquals(Long studentId, Long courseId, EnrollmentStatus status);
}