package id.ac.ui.cs.advprog.udehnihcourse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a Section (Chapter/Module) within a Course.
 * Design Pattern: Entity (Domain Model)
 */
@Entity
@Table(name = "sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    public void addArticle(Article article) {
        articles.add(article);
        article.setSection(this);
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.setSection(null);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return id != null && id.equals(section.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
