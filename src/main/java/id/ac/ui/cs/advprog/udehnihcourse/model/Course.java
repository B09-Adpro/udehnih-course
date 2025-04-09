package id.ac.ui.cs.advprog.udehnihcourse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Builder.Default;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents an E-Learning Course.
 * Design Pattern: Entity (Domain Model) - Represents core domain data.
 * Design Pattern: Builder (via Lombok @Builder) - For flexible object creation.
 */
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column
    private String category;

    @Column(name = "tutor_id", nullable = false)
    private String tutorId;

    @Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Default
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
        section.setCourse(this);
    }

    public void removeSection(Section section) {
        sections.remove(section);
        section.setCourse(null);
    }

    // TODO: implement logic-nya
    @Transient
    private int enrollmentCount = 0;
}
