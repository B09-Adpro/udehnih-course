package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
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
public class CourseRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    private Course course1;
    private Course course2;
    private String tutor1Id = "tutor-1";
    private String tutor2Id = "tutor-2";

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();

        course1 = Course.builder()
                .title("Course A")
                .description("Description A")
                .tutorId(tutor1Id)
                .price(new BigDecimal("100.00"))
                .build();

        course2 = Course.builder()
                .title("Course B")
                .description("Description B")
                .tutorId(tutor1Id)
                .price(BigDecimal.ZERO)
                .build();
    }

    @Test
    void whenSaveCourse_thenFindById_returnsCourse() {
        Course savedCourse = courseRepository.save(course1);

        Optional<Course> foundCourseOpt = courseRepository.findById(savedCourse.getId());

        assertTrue(foundCourseOpt.isPresent());
        Course foundCourse = foundCourseOpt.get();
        assertEquals(savedCourse.getId(), foundCourse.getId());
        assertEquals("Course A", foundCourse.getTitle());
        assertEquals(tutor1Id, foundCourse.getTutorId());
        assertEquals(0, new BigDecimal("100.00").compareTo(foundCourse.getPrice()));
        assertNotNull(foundCourse.getCreatedAt());
    }


    @Test
    void whenSaveCourseWithEntityManager_thenFindById_returnsCourse() {
        Course persistedCourse = entityManager.persistFlushFind(course1);

        Optional<Course> foundCourseOpt = courseRepository.findById(persistedCourse.getId());

        assertTrue(foundCourseOpt.isPresent());
        Course foundCourse = foundCourseOpt.get();
        assertEquals(persistedCourse.getId(), foundCourse.getId());
        assertEquals("Course A", foundCourse.getTitle());
    }

    @Test
    void whenFindAll_thenReturnListOfCourses() {
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.flush();

        List<Course> courses = courseRepository.findAll();

        assertNotNull(courses);
        assertEquals(2, courses.size());
        assertTrue(courses.stream().anyMatch(c -> c.getTitle().equals("Course A")));
        assertTrue(courses.stream().anyMatch(c -> c.getTitle().equals("Course B")));
    }

    @Test
    void whenDeleteCourse_thenFindById_returnsEmpty() {
        Course savedCourse = entityManager.persistFlushFind(course1);
        Long courseId = savedCourse.getId();
        assertNotNull(courseId);

        courseRepository.deleteById(courseId);
        // entityManager.flush();

        Optional<Course> foundCourseOpt = courseRepository.findById(courseId);
        assertFalse(foundCourseOpt.isPresent());
    }

    @Test
    void whenFindByTutorId_thenReturnCorrectCourses() {
        Course course3 = Course.builder()
                .title("Course C")
                .tutorId(tutor2Id)
                .build();

        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.persist(course3);
        entityManager.flush();

        List<Course> tutor1Courses = courseRepository.findByTutorId(tutor1Id);
        List<Course> tutor2Courses = courseRepository.findByTutorId(tutor2Id);
        List<Course> nonExistentTutorCourses = courseRepository.findByTutorId("tutor-nonexistent");


        assertEquals(2, tutor1Courses.size());
        assertTrue(tutor1Courses.stream().allMatch(c -> c.getTutorId().equals(tutor1Id)));
        assertTrue(tutor1Courses.stream().anyMatch(c -> c.getTitle().equals("Course A")));
        assertTrue(tutor1Courses.stream().anyMatch(c -> c.getTitle().equals("Course B")));

        assertEquals(1, tutor2Courses.size());
        assertEquals("Course C", tutor2Courses.get(0).getTitle());

        assertTrue(nonExistentTutorCourses.isEmpty());
    }
}
