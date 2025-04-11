package id.ac.ui.cs.advprog.udehnihcourse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO for simple message responses (e.g., Delete Tutor Application).
 * Design Pattern: DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse {
    private String message;
}
