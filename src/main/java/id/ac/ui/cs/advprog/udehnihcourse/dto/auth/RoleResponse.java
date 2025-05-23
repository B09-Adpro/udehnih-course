package id.ac.ui.cs.advprog.udehnihcourse.dto.auth;

import id.ac.ui.cs.advprog.udehnihcourse.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private boolean success;
    private String message;
    private Long userId;
    private RoleType roleType;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}