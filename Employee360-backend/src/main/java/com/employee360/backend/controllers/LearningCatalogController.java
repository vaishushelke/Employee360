package com.employee360.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee360.backend.dto.request.LearningTopicRequest;
import com.employee360.backend.dto.response.LearningCatalogResponse;
import com.employee360.backend.dto.response.LearningTopicAdminResponse;
import com.employee360.backend.services.LearningCatalogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/learning/catalog")
public class LearningCatalogController {

    private final LearningCatalogService learningCatalogService;

    public LearningCatalogController(
            LearningCatalogService learningCatalogService) {
        this.learningCatalogService = learningCatalogService;
    }

    @GetMapping
    public ResponseEntity<LearningCatalogResponse> getCatalog() {
        return ResponseEntity.ok(learningCatalogService.getCatalog());
    }

    @PostMapping("/topics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LearningTopicAdminResponse> createTopic(
            @Valid @RequestBody LearningTopicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningCatalogService.createTopic(request));
    }

    @PutMapping("/topics/{topicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LearningTopicAdminResponse> updateTopic(
            @PathVariable Long topicId,
            @Valid @RequestBody LearningTopicRequest request) {
        return ResponseEntity.ok(learningCatalogService.updateTopic(topicId, request));
    }

    @DeleteMapping("/topics/{topicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> archiveTopic(@PathVariable Long topicId) {
        learningCatalogService.archiveTopic(topicId);
        return ResponseEntity.noContent().build();
    }
}
