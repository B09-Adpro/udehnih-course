package id.ac.ui.cs.advprog.udehnihcourse.controller;

import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.article.ArticleResponse;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionRequest;
import id.ac.ui.cs.advprog.udehnihcourse.dto.section.SectionResponse;
import id.ac.ui.cs.advprog.udehnihcourse.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihcourse.service.CourseContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
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
            @Valid @RequestBody SectionRequest sectionRequest,
            @AuthenticationPrincipal AppUserDetails tutorDetails
            ) {

        String tutorId = String.valueOf(tutorDetails.getId());

        try {
            SectionResponse createdSection = courseContentService.addSectionToCourse(courseId, sectionRequest, tutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSection);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @GetMapping("/courses/{courseId}/sections")
    public ResponseEntity<List<SectionResponse>> getSections(
            @PathVariable Long courseId,
            @AuthenticationPrincipal AppUserDetails userDetails) {

        String userId = String.valueOf(userDetails.getId());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        List<SectionResponse> sections = courseContentService.getSectionsByCourseForUser(courseId, userId, authorities);
        return ResponseEntity.ok(sections);
    }

    @PutMapping("/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionRequest sectionRequest,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {
        String tutorId = String.valueOf(tutorDetails.getId());

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
            @PathVariable Long sectionId,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {

        String tutorId = String.valueOf(tutorDetails.getId());

        try {
            courseContentService.deleteSection(courseId, sectionId, tutorId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @PostMapping("/courses/{courseId}/sections/{sectionId}/articles")
    public ResponseEntity<ArticleResponse> addArticle(
            @PathVariable Long sectionId,
            @Valid @RequestBody ArticleRequest articleRequest,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {

        String tutorId = String.valueOf(tutorDetails.getId());

        try {
            ArticleResponse createdArticle = courseContentService.addArticleToSection(sectionId, articleRequest, tutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @GetMapping("/courses/{courseId}/sections/{sectionId}/articles")
    public ResponseEntity<List<ArticleResponse>> getArticles(
            @PathVariable Long sectionId,
            @AuthenticationPrincipal AppUserDetails userDetails
            ) {
        String userId = String.valueOf(userDetails.getId());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        List<ArticleResponse> articles = courseContentService.getArticlesBySectionForUser(sectionId, userId, authorities);
        return ResponseEntity.ok(articles);
    }


    @PutMapping("/courses/{courseId}/sections/{sectionId}/articles/{articleId}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long sectionId,
            @PathVariable Long articleId,
            @Valid @RequestBody ArticleRequest articleRequest,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {

        String tutorId = String.valueOf(tutorDetails.getId());

        try {
            ArticleResponse updatedArticle = courseContentService.updateArticle(sectionId, articleId, articleRequest, tutorId);
            return ResponseEntity.ok(updatedArticle);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

    @DeleteMapping("/courses/{courseId}/sections/{sectionId}/articles/{articleId}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable Long sectionId,
            @PathVariable Long articleId,
            @AuthenticationPrincipal AppUserDetails tutorDetails
    ) {

        String tutorId = String.valueOf(tutorDetails.getId());

        try {
            courseContentService.deleteArticle(sectionId, articleId, tutorId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }
}