package id.ac.ui.cs.advprog.udehnihcourse.dto.section;

import id.ac.ui.cs.advprog.udehnihcourse.model.Section;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Section response.
 * Design Pattern: DTO, Static Factory Method
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {
    private Long id;
    private String title;
    private Long courseId;

    public static SectionResponse fromEntity(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .title(section.getTitle())
                .courseId(section.getCourse() != null ? section.getCourse().getId() : null)
                .build();
    }
}