package id.ac.ui.cs.advprog.udehnihcourse.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentStatusTest {

    @Test
    void testEnrollmentStatusValues() {
        EnrollmentStatus[] statuses = EnrollmentStatus.values();
        assertEquals(4, statuses.length);
        assertEquals(EnrollmentStatus.ENROLLED, statuses[0]);
    }

    @Test
    void testEnrollmentStatusValueOf() {
        assertEquals(EnrollmentStatus.ENROLLED, EnrollmentStatus.valueOf("ENROLLED"));
    }

    @Test
    void testEnrollmentStatusToString() {
        assertEquals("ENROLLED", EnrollmentStatus.ENROLLED.toString());
    }

    @Test
    void testEnrollmentStatusInvalidValue() {
        assertThrows(IllegalArgumentException.class, () ->
                EnrollmentStatus.valueOf("INVALID_STATUS")
        );
    }
}