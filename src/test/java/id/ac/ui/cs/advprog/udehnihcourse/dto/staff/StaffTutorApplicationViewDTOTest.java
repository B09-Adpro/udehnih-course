package id.ac.ui.cs.advprog.udehnihcourse.dto.staff;

import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistration;
import id.ac.ui.cs.advprog.udehnihcourse.model.TutorRegistrationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StaffTutorApplicationViewDTOTest {

    private TutorRegistration tutorRegistration;
    private LocalDateTime submittedAt;
    private LocalDateTime processedAt;

    @BeforeEach
    void setUp() {
        submittedAt = LocalDateTime.now().minusDays(2);
        processedAt = LocalDateTime.now().minusDays(1);

        tutorRegistration = new TutorRegistration();
        tutorRegistration.setId(1L);
        tutorRegistration.setStudentId("student-123");
        tutorRegistration.setExperience("5 years");
        tutorRegistration.setQualifications("PhD");
        tutorRegistration.setBio("Passionate teacher");
        tutorRegistration.setStatus(TutorRegistrationStatus.PENDING);
        tutorRegistration.setSubmittedAt(submittedAt);
        tutorRegistration.setProcessedAt(processedAt);
    }

    @Test
    void fromEntity_shouldMapAllFields() {
        StaffTutorApplicationViewDTO dto = StaffTutorApplicationViewDTO.fromEntity(tutorRegistration);

        assertNotNull(dto);
        assertEquals(tutorRegistration.getId(), dto.getApplicationId());
        assertEquals(tutorRegistration.getStudentId(), dto.getStudentId());
        assertEquals(tutorRegistration.getExperience(), dto.getExperience());
        assertEquals(tutorRegistration.getQualifications(), dto.getQualifications());
        assertEquals(tutorRegistration.getBio(), dto.getBio());
        assertEquals(tutorRegistration.getStatus(), dto.getStatus());
        assertEquals(tutorRegistration.getSubmittedAt(), dto.getSubmittedAt());
        assertEquals(tutorRegistration.getProcessedAt(), dto.getProcessedAt());
    }

    @Test
    void fromEntity_whenProcessedAtIsNull_shouldHandleGracefully() {
        tutorRegistration.setProcessedAt(null);

        StaffTutorApplicationViewDTO dto = StaffTutorApplicationViewDTO.fromEntity(tutorRegistration);

        assertNotNull(dto);
        assertEquals(tutorRegistration.getId(), dto.getApplicationId());
        assertNull(dto.getProcessedAt());
        assertEquals(tutorRegistration.getSubmittedAt(), dto.getSubmittedAt());
    }

    @Test
    void fromEntity_withDifferentStatuses_shouldMapCorrectly() {
        // Test ACCEPTED status
        tutorRegistration.setStatus(TutorRegistrationStatus.ACCEPTED);
        StaffTutorApplicationViewDTO acceptedDto = StaffTutorApplicationViewDTO.fromEntity(tutorRegistration);
        assertEquals(TutorRegistrationStatus.ACCEPTED, acceptedDto.getStatus());

        // Test DENIED status
        tutorRegistration.setStatus(TutorRegistrationStatus.DENIED);
        StaffTutorApplicationViewDTO deniedDto = StaffTutorApplicationViewDTO.fromEntity(tutorRegistration);
        assertEquals(TutorRegistrationStatus.DENIED, deniedDto.getStatus());

        // Test PENDING status
        tutorRegistration.setStatus(TutorRegistrationStatus.PENDING);
        StaffTutorApplicationViewDTO pendingDto = StaffTutorApplicationViewDTO.fromEntity(tutorRegistration);
        assertEquals(TutorRegistrationStatus.PENDING, pendingDto.getStatus());
    }

    @Test
    void fromEntity_withNullTutorRegistration_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            StaffTutorApplicationViewDTO.fromEntity(null);
        });
    }

    @Test
    void builder_shouldWorkCorrectly() {
        LocalDateTime testTime = LocalDateTime.now();

        StaffTutorApplicationViewDTO dto = StaffTutorApplicationViewDTO.builder()
                .applicationId(2L)
                .studentId("student-456")
                .experience("10 years experience")
                .qualifications("Master's degree")
                .bio("Experienced educator")
                .status(TutorRegistrationStatus.ACCEPTED)
                .submittedAt(testTime)
                .processedAt(testTime.plusDays(1))
                .build();

        assertEquals(2L, dto.getApplicationId());
        assertEquals("student-456", dto.getStudentId());
        assertEquals("10 years experience", dto.getExperience());
        assertEquals("Master's degree", dto.getQualifications());
        assertEquals("Experienced educator", dto.getBio());
        assertEquals(TutorRegistrationStatus.ACCEPTED, dto.getStatus());
        assertEquals(testTime, dto.getSubmittedAt());
        assertEquals(testTime.plusDays(1), dto.getProcessedAt());
    }

    @Test
    void noArgsConstructor_shouldWork() {
        StaffTutorApplicationViewDTO dto = new StaffTutorApplicationViewDTO();

        assertNotNull(dto);
        assertNull(dto.getApplicationId());
        assertNull(dto.getStudentId());
        assertNull(dto.getExperience());
        assertNull(dto.getQualifications());
        assertNull(dto.getBio());
        assertNull(dto.getStatus());
        assertNull(dto.getSubmittedAt());
        assertNull(dto.getProcessedAt());
    }

    @Test
    void allArgsConstructor_shouldWork() {
        LocalDateTime testTime = LocalDateTime.now();

        StaffTutorApplicationViewDTO dto = new StaffTutorApplicationViewDTO(
                3L,
                "student-789",
                "15 years",
                "PhD in Education",
                "Expert teacher",
                TutorRegistrationStatus.DENIED,
                testTime,
                testTime.plusDays(2)
        );

        assertEquals(3L, dto.getApplicationId());
        assertEquals("student-789", dto.getStudentId());
        assertEquals("15 years", dto.getExperience());
        assertEquals("PhD in Education", dto.getQualifications());
        assertEquals("Expert teacher", dto.getBio());
        assertEquals(TutorRegistrationStatus.DENIED, dto.getStatus());
        assertEquals(testTime, dto.getSubmittedAt());
        assertEquals(testTime.plusDays(2), dto.getProcessedAt());
    }

    @Test
    void settersAndGetters_shouldWork() {
        StaffTutorApplicationViewDTO dto = new StaffTutorApplicationViewDTO();
        LocalDateTime testTime = LocalDateTime.now();

        dto.setApplicationId(4L);
        dto.setStudentId("student-setter");
        dto.setExperience("Setter experience");
        dto.setQualifications("Setter qualifications");
        dto.setBio("Setter bio");
        dto.setStatus(TutorRegistrationStatus.PENDING);
        dto.setSubmittedAt(testTime);
        dto.setProcessedAt(testTime.plusDays(3));

        assertEquals(4L, dto.getApplicationId());
        assertEquals("student-setter", dto.getStudentId());
        assertEquals("Setter experience", dto.getExperience());
        assertEquals("Setter qualifications", dto.getQualifications());
        assertEquals("Setter bio", dto.getBio());
        assertEquals(TutorRegistrationStatus.PENDING, dto.getStatus());
        assertEquals(testTime, dto.getSubmittedAt());
        assertEquals(testTime.plusDays(3), dto.getProcessedAt());
    }

    @Test
    void fromEntity_withLongTextFields_shouldHandleCorrectly() {
        String longExperience = "A".repeat(1000);
        String longQualifications = "B".repeat(500);
        String longBio = "C".repeat(300);

        tutorRegistration.setExperience(longExperience);
        tutorRegistration.setQualifications(longQualifications);
        tutorRegistration.setBio(longBio);

        StaffTutorApplicationViewDTO dto = StaffTutorApplicationViewDTO.fromEntity(tutorRegistration);

        assertEquals(longExperience, dto.getExperience());
        assertEquals(longQualifications, dto.getQualifications());
        assertEquals(longBio, dto.getBio());
    }
}