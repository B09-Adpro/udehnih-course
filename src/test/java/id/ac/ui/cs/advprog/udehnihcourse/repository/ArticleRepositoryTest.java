package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Article;
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
public class ArticleRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArticleRepository articleRepository;

    private Course course;
    private Section section;
    private Article article1;

    @BeforeEach
    void setUp() {
        course = Course.builder().title("Course for Articles").tutorId("tutor-art").build();
        entityManager.persist(course);

        section = new Section();
        section.setTitle("Section for Articles");
        section.setCourse(course);
        entityManager.persist(section);

        article1 = new Article();
        article1.setTitle("Article 1");
        article1.setContent("Content 1");
        article1.setSection(section);
    }

    @Test
    void whenSaveArticle_thenFindById_returnsArticle() {
        Article savedArticle = articleRepository.save(article1);
        assertNotNull(savedArticle.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Article> foundArticleOpt = articleRepository.findById(savedArticle.getId());

        assertTrue(foundArticleOpt.isPresent());
        Article foundArticle = foundArticleOpt.get();
        assertEquals(savedArticle.getId(), foundArticle.getId());
        assertEquals("Article 1", foundArticle.getTitle());
        assertEquals("Content 1", foundArticle.getContent());
        assertNotNull(foundArticle.getSection());
        assertEquals(section.getId(), foundArticle.getSection().getId());
        assertEquals("Section for Articles", foundArticle.getSection().getTitle());
    }

    @Test
    void whenDeleteArticle_thenFindById_returnsEmpty() {
        Article savedArticle = articleRepository.save(article1);
        Long articleId = savedArticle.getId();
        entityManager.flush();

        articleRepository.deleteById(articleId);
        entityManager.flush();

        Optional<Article> foundArticleOpt = articleRepository.findById(articleId);
        assertFalse(foundArticleOpt.isPresent());

        Section foundSection = entityManager.find(Section.class, section.getId());
        assertNotNull(foundSection);
    }
}
