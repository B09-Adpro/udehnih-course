package id.ac.ui.cs.advprog.udehnihcourse.repository;

import id.ac.ui.cs.advprog.udehnihcourse.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{
    List<Article> findBySectionId(Long sectionId);
    Optional<Article> findByIdAndSectionId(Long articleId, Long sectionId);
    long countBySectionId(Long sectionId);
}
