package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionResponse;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * REST Controller for managing Sections and Articles within a Course by Tutors.
 * Design Pattern: Controller (REST)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseContentController {

    private final CourseContentService courseContentService;

    @PostMapping("/courses/{courseId}/sections")
    public ResponseEntity<SectionResponse> addSection(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionRequest sectionRequest) {
        // TODO: Get authenticated Tutor ID from Security Context
        String tutorId = "tutor-content-placeholder";

        try {
            SectionResponse createdSection = courseContentService.addSectionToCourse(courseId, sectionRequest, tutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSection);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @GetMapping("/courses/{courseId}/sections")
    public ResponseEntity<List<SectionResponse>> getSections(@PathVariable Long courseId) {
        List<SectionResponse> sections = courseContentService.getSectionsByCourse(courseId);
        return ResponseEntity.ok(sections);
    }

    @PutMapping("/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionRequest sectionRequest) {
        // TODO: Get authenticated Tutor ID
        String tutorId = "tutor-content-placeholder";

        try {
            SectionResponse updatedSection = courseContentService.updateSection(courseId, sectionId, sectionRequest, tutorId);
            return ResponseEntity.ok(updatedSection);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @DeleteMapping("/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId) {
        // TODO: Get authenticated Tutor ID
        String tutorId = "tutor-content-placeholder";

        try {
            courseContentService.deleteSection(courseId, sectionId, tutorId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @PostMapping("/sections/{sectionId}/articles")
    public ResponseEntity<ArticleResponse> addArticle(
            @PathVariable Long sectionId,
            @Valid @RequestBody ArticleRequest articleRequest) {
        // TODO: Get authenticated Tutor ID
        String tutorId = "tutor-content-placeholder";

        try {
            ArticleResponse createdArticle = courseContentService.addArticleToSection(sectionId, articleRequest, tutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @GetMapping("/sections/{sectionId}/articles")
    public ResponseEntity<List<ArticleResponse>> getArticles(@PathVariable Long sectionId) {
        List<ArticleResponse> articles = courseContentService.getArticlesBySection(sectionId);
        return ResponseEntity.ok(articles);
    }


    @PutMapping("/sections/{sectionId}/articles/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long sectionId,
            @PathVariable Long articleId,
            @Valid @RequestBody ArticleRequest articleRequest) {
        // TODO: Get authenticated Tutor ID
        String tutorId = "tutor-content-placeholder";

        try {
            ArticleResponse updatedArticle = courseContentService.updateArticle(sectionId, articleId, articleRequest, tutorId);
            return ResponseEntity.ok(updatedArticle);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @DeleteMapping("/sections/{sectionId}/articles/{articleId}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable Long sectionId,
            @PathVariable Long articleId) {
        // TODO: Get authenticated Tutor ID
        String tutorId = "tutor-content-placeholder";

        try {
            courseContentService.deleteArticle(sectionId, articleId, tutorId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }
}