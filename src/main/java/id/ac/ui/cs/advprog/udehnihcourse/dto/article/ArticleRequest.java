package id.ac.ui.cs.advprog.udehnihcourse.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleRequest {
    @NotBlank(message = "Article title is mandatory")
    @Size(max = 255, message = "Article title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Article content is mandatory")
    private String content;

    @NotBlank(message = "Content type is mandatory (e.g., TEXT, IMAGE_URL, VIDEO_LINK)")
    @Size(max = 50)
    private String contentType;
}