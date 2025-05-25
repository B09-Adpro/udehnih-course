package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
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
public class TutorRegistrationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TutorRegistrationRepository tutorRegistrationRepository;

    private TutorRegistration pendingRegistration;
    private TutorRegistration acceptedRegistration;
    private TutorRegistration deniedRegistration;

    private String studentId1 = "student-123";
    private String studentId2 = "student-456";
    private String studentId3 = "student-789";

    @BeforeEach
    void setUp() {
        tutorRegistrationRepository.deleteAll();
        entityManager.clear();

        pendingRegistration = new TutorRegistration(
                studentId1,
                "5 years teaching experience",
                "Master's in Computer Science",
                "Passionate about teaching programming"
        );
        pendingRegistration.setStatus(TutorRegistrationStatus.PENDING);

        acceptedRegistration = new TutorRegistration(
                studentId2,
                "10 years industry experience",
                "PhD in Software Engineering",
                "Expert in web development"
        );
        acceptedRegistration.setStatus(TutorRegistrationStatus.ACCEPTED);

        deniedRegistration = new TutorRegistration(
                studentId3,
                "2 years experience",
                "Bachelor's degree",
                "New to teaching"
        );
        deniedRegistration.setStatus(TutorRegistrationStatus.DENIED);
    }

    @Test
    void whenSaveTutorRegistration_thenFindById_returnsTutorRegistration() {
        TutorRegistration savedRegistration = tutorRegistrationRepository.save(pendingRegistration);

        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findById(savedRegistration.getId());

        assertTrue(foundRegistrationOpt.isPresent());
        TutorRegistration foundRegistration = foundRegistrationOpt.get();
        assertEquals(savedRegistration.getId(), foundRegistration.getId());
        assertEquals(studentId1, foundRegistration.getStudentId());
        assertEquals("5 years teaching experience", foundRegistration.getExperience());
        assertEquals("Master's in Computer Science", foundRegistration.getQualifications());
        assertEquals("Passionate about teaching programming", foundRegistration.getBio());
        assertEquals(TutorRegistrationStatus.PENDING, foundRegistration.getStatus());
        assertNotNull(foundRegistration.getSubmittedAt());
    }

    @Test
    void findByStudentId_whenExists_shouldReturnTutorRegistration() {
        entityManager.persist(pendingRegistration);
        entityManager.flush();

        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findByStudentId(studentId1);

        assertTrue(foundRegistrationOpt.isPresent());
        TutorRegistration foundRegistration = foundRegistrationOpt.get();
        assertEquals(studentId1, foundRegistration.getStudentId());
        assertEquals(TutorRegistrationStatus.PENDING, foundRegistration.getStatus());
    }

    @Test
    void findByStudentId_whenNotExists_shouldReturnEmpty() {
        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findByStudentId("non-existent");

        assertFalse(foundRegistrationOpt.isPresent());
    }

    @Test
    void existsByStudentId_whenExists_shouldReturnTrue() {
        entityManager.persist(pendingRegistration);
        entityManager.flush();

        boolean exists = tutorRegistrationRepository.existsByStudentId(studentId1);

        assertTrue(exists);
    }

    @Test
    void existsByStudentId_whenNotExists_shouldReturnFalse() {
        boolean exists = tutorRegistrationRepository.existsByStudentId("non-existent");

        assertFalse(exists);
    }

    @Test
    void findByStatus_shouldReturnRegistrationsWithSpecificStatus() {
        entityManager.persist(pendingRegistration);
        entityManager.persist(acceptedRegistration);
        entityManager.persist(deniedRegistration);
        entityManager.flush();

        List<TutorRegistration> pendingRegistrations = tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.PENDING);
        List<TutorRegistration> acceptedRegistrations = tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.ACCEPTED);
        List<TutorRegistration> deniedRegistrations = tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.DENIED);

        assertEquals(1, pendingRegistrations.size());
        assertEquals(studentId1, pendingRegistrations.get(0).getStudentId());

        assertEquals(1, acceptedRegistrations.size());
        assertEquals(studentId2, acceptedRegistrations.get(0).getStudentId());

        assertEquals(1, deniedRegistrations.size());
        assertEquals(studentId3, deniedRegistrations.get(0).getStudentId());
    }

    @Test
    void findByStatus_whenNoRegistrationsWithStatus_shouldReturnEmptyList() {
        entityManager.persist(pendingRegistration);
        entityManager.flush();

        List<TutorRegistration> acceptedRegistrations = tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.ACCEPTED);

        assertNotNull(acceptedRegistrations);
        assertTrue(acceptedRegistrations.isEmpty());
    }

    @Test
    void findByStudentIdAndStatus_whenExists_shouldReturnTutorRegistration() {
        entityManager.persist(acceptedRegistration);
        entityManager.flush();

        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findByStudentIdAndStatus(
                studentId2, TutorRegistrationStatus.ACCEPTED);

        assertTrue(foundRegistrationOpt.isPresent());
        TutorRegistration foundRegistration = foundRegistrationOpt.get();
        assertEquals(studentId2, foundRegistration.getStudentId());
        assertEquals(TutorRegistrationStatus.ACCEPTED, foundRegistration.getStatus());
    }

    @Test
    void findByStudentIdAndStatus_whenStudentExistsButDifferentStatus_shouldReturnEmpty() {
        entityManager.persist(pendingRegistration);
        entityManager.flush();

        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findByStudentIdAndStatus(
                studentId1, TutorRegistrationStatus.ACCEPTED);

        assertFalse(foundRegistrationOpt.isPresent());
    }

    @Test
    void findByStudentIdAndStatus_whenStudentNotExists_shouldReturnEmpty() {
        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findByStudentIdAndStatus(
                "non-existent", TutorRegistrationStatus.PENDING);

        assertFalse(foundRegistrationOpt.isPresent());
    }

    @Test
    void whenDeleteTutorRegistration_thenFindById_returnsEmpty() {
        TutorRegistration savedRegistration = entityManager.persistFlushFind(pendingRegistration);
        Long registrationId = savedRegistration.getId();

        tutorRegistrationRepository.deleteById(registrationId);
        entityManager.flush();

        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findById(registrationId);
        assertFalse(foundRegistrationOpt.isPresent());
    }

    @Test
    void findAll_shouldReturnAllRegistrations() {
        entityManager.persist(pendingRegistration);
        entityManager.persist(acceptedRegistration);
        entityManager.persist(deniedRegistration);
        entityManager.flush();

        List<TutorRegistration> allRegistrations = tutorRegistrationRepository.findAll();

        assertEquals(3, allRegistrations.size());
        assertTrue(allRegistrations.stream().anyMatch(r -> r.getStudentId().equals(studentId1)));
        assertTrue(allRegistrations.stream().anyMatch(r -> r.getStudentId().equals(studentId2)));
        assertTrue(allRegistrations.stream().anyMatch(r -> r.getStudentId().equals(studentId3)));
    }

    @Test
    void uniqueConstraintOnStudentId_shouldPreventDuplicates() {
        entityManager.persist(pendingRegistration);
        entityManager.flush();

        // Try to create another registration for the same student
        TutorRegistration duplicateRegistration = new TutorRegistration(
                studentId1, // Same student ID
                "Different experience",
                "Different qualifications",
                "Different bio"
        );

        // This should throw an exception due to unique constraint
        assertThrows(Exception.class, () -> {
            entityManager.persist(duplicateRegistration);
            entityManager.flush();
        });
    }

    @Test
    void statusUpdate_shouldPersistCorrectly() {
        TutorRegistration savedRegistration = tutorRegistrationRepository.save(pendingRegistration);
        entityManager.flush();
        entityManager.clear();

        // Update status
        savedRegistration.setStatus(TutorRegistrationStatus.ACCEPTED);
        TutorRegistration updatedRegistration = tutorRegistrationRepository.save(savedRegistration);
        entityManager.flush();
        entityManager.clear();

        // Verify update
        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findById(updatedRegistration.getId());
        assertTrue(foundRegistrationOpt.isPresent());
        assertEquals(TutorRegistrationStatus.ACCEPTED, foundRegistrationOpt.get().getStatus());
    }

    @Test
    void submittedAtTimestamp_shouldBeAutoGenerated() {
        TutorRegistration savedRegistration = tutorRegistrationRepository.save(pendingRegistration);
        entityManager.flush();
        entityManager.clear();

        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findById(savedRegistration.getId());
        assertTrue(foundRegistrationOpt.isPresent());

        TutorRegistration foundRegistration = foundRegistrationOpt.get();
        assertNotNull(foundRegistration.getSubmittedAt());
    }

    @Test
    void defaultStatus_shouldBePending() {
        TutorRegistration newRegistration = new TutorRegistration(
                "new-student",
                "Experience",
                "Qualifications",
                "Bio"
        ); // Default constructor sets PENDING status

        TutorRegistration savedRegistration = tutorRegistrationRepository.save(newRegistration);

        assertEquals(TutorRegistrationStatus.PENDING, savedRegistration.getStatus());
    }

    @Test
    void multipleStatusQueries_shouldWorkIndependently() {
        // Create registrations with different statuses
        entityManager.persist(pendingRegistration);
        entityManager.persist(acceptedRegistration);
        entityManager.persist(deniedRegistration);
        entityManager.flush();

        // Query for each status
        List<TutorRegistration> pending = tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.PENDING);
        List<TutorRegistration> accepted = tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.ACCEPTED);
        List<TutorRegistration> denied = tutorRegistrationRepository.findByStatus(TutorRegistrationStatus.DENIED);

        // Verify each query returns correct results
        assertEquals(1, pending.size());
        assertEquals(1, accepted.size());
        assertEquals(1, denied.size());

        assertEquals(TutorRegistrationStatus.PENDING, pending.get(0).getStatus());
        assertEquals(TutorRegistrationStatus.ACCEPTED, accepted.get(0).getStatus());
        assertEquals(TutorRegistrationStatus.DENIED, denied.get(0).getStatus());
    }

    @Test
    void textFields_shouldHandleLongContent() {
        String longExperience = "A".repeat(2000);
        String longQualifications = "B".repeat(1000);
        String longBio = "C".repeat(500);

        TutorRegistration longContentRegistration = new TutorRegistration(
                "long-content-student",
                longExperience,
                longQualifications,
                longBio
        );

        TutorRegistration savedRegistration = tutorRegistrationRepository.save(longContentRegistration);
        entityManager.flush();
        entityManager.clear();

        Optional<TutorRegistration> foundRegistrationOpt = tutorRegistrationRepository.findById(savedRegistration.getId());
        assertTrue(foundRegistrationOpt.isPresent());

        TutorRegistration foundRegistration = foundRegistrationOpt.get();
        assertEquals(longExperience, foundRegistration.getExperience());
        assertEquals(longQualifications, foundRegistration.getQualifications());
        assertEquals(longBio, foundRegistration.getBio());
    }
}