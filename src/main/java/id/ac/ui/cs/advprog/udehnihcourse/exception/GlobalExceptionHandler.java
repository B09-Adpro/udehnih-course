package id.ac.ui.cs.advprog.udehnihcourse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCourseNotFound(CourseNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Kursus tidak ditemukan");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleArticleNotFound(ArticleNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Artikel tidak ditemukan");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(SectionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSectionNotFound(SectionNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Bagian tidak ditemukan");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AlreadyEnrolledException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyEnrolled(AlreadyEnrolledException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Sudah terdaftar");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEnrollmentNotFound(EnrollmentNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Pendaftaran tidak ditemukan");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PaymentInitiationFailedException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentInitiationFailed(PaymentInitiationFailedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Gagal menginisiasi pembayaran");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Akses tidak diizinkan");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}