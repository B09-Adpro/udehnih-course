package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Course;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>{

    List<Course> findByTutorId(String tutorId);
    List<Course> findByTitleContainingIgnoreCase(String keyword);
    
    @EntityGraph(attributePaths = {"sections", "sections.articles"})
    Optional<Course> findById(Long id);
}
