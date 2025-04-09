package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import id.ac.ui.cs.advprog.udehnihcourse.model.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class SectionRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SectionRepository sectionRepository;

    private Course course;
    private Section section1;

    @BeforeEach
    void setUp() {
        course = Course.builder().title("Course for Sections").tutorId("tutor-sec").build();
        entityManager.persist(course);

        section1 = new Section();
        section1.setTitle("Section 1");
        section1.setCourse(course);
    }

    @Test
    void whenSaveSection_thenFindById_returnsSection() {
        Section savedSection = sectionRepository.save(section1);
        assertNotNull(savedSection.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Section> foundSectionOpt = sectionRepository.findById(savedSection.getId());

        assertTrue(foundSectionOpt.isPresent());
        Section foundSection = foundSectionOpt.get();
        assertEquals(savedSection.getId(), foundSection.getId());
        assertEquals("Section 1", foundSection.getTitle());
        assertNotNull(foundSection.getCourse());
        assertEquals(course.getId(), foundSection.getCourse().getId()); // Check correct course loaded
        assertEquals("Course for Sections", foundSection.getCourse().getTitle());
    }

    @Test
    void whenDeleteSection_thenFindById_returnsEmpty() {
        Section savedSection = sectionRepository.save(section1);
        Long sectionId = savedSection.getId();
        entityManager.flush();

        sectionRepository.deleteById(sectionId);
        entityManager.flush();

        Optional<Section> foundSectionOpt = sectionRepository.findById(sectionId);
        assertFalse(foundSectionOpt.isPresent());

        Course foundCourse = entityManager.find(Course.class, course.getId());
        assertNotNull(foundCourse);
    }
}
