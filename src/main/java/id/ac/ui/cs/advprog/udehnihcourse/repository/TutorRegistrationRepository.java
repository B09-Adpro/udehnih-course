package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TutorRegistration entities.
 * Design Pattern: Repository (via Spring Data JPA) - Abstracts data access logic.
 */
public interface TutorRegistrationRepository extends JpaRepository<TutorRegistration, Long>{
    Optional<TutorRegistration> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
    List<TutorRegistration> findByStatus(TutorRegistrationStatus status);
    Optional<TutorRegistration> findByStudentIdAndStatus(String studentId, TutorRegistrationStatus status);

}
