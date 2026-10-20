
package com.employee360.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.employee360.backend.dto.request.QuizCreateRequest;
import com.employee360.backend.dto.request.QuizQuestionRequest;
import com.employee360.backend.dto.request.QuizSubmissionRequest;
import com.employee360.backend.dto.response.QuizQuestionResponse;
import com.employee360.backend.dto.response.QuizResponse;
import com.employee360.backend.dto.response.QuizResultResponse;
import com.employee360.backend.services.QuizService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // ADMIN: Create a quiz
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponse> createQuiz(
            @Valid @RequestBody QuizCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.createQuiz(request));
    }

        @GetMapping("/admin")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<QuizResponse>> getAllQuizzesForAdmin() {
                return ResponseEntity.ok(quizService.getAllQuizzesForAdmin());
        }

        @PutMapping("/{quizId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<QuizResponse> updateQuiz(
                        @PathVariable Long quizId,
                        @Valid @RequestBody QuizCreateRequest request) {
                return ResponseEntity.ok(quizService.updateQuiz(quizId, request));
        }

        @DeleteMapping("/{quizId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> archiveQuiz(@PathVariable Long quizId) {
                quizService.archiveQuiz(quizId);
                return ResponseEntity.noContent().build();
        }

    // View active quizzes
    @GetMapping
    public ResponseEntity<List<QuizResponse>> getActiveQuizzes() {

        return ResponseEntity.ok(quizService.getActiveQuizzes());
    }

    // View quiz details
    @GetMapping("/{quizId}")
        @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizResponse> getQuizById(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(quizService.getQuizById(quizId));
    }

    // ADMIN: Add a question and its options
    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizQuestionResponse> addQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizQuestionRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.addQuestion(quizId, request));
    }

    // View questions without correct-answer information
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<QuizQuestionResponse>> getQuizQuestions(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                quizService.getQuizQuestions(quizId));
    }

        @GetMapping("/admin/{quizId}/questions")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<QuizQuestionResponse>> getQuizQuestionsForAdmin(
                        @PathVariable Long quizId) {
                return ResponseEntity.ok(quizService.getQuizQuestionsForAdmin(quizId));
        }

    // Start a quiz attempt
    @PostMapping("/{quizId}/start")
    public ResponseEntity<QuizResultResponse> startQuiz(
            @PathVariable Long quizId,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.startQuiz(
                        quizId, authentication.getName()));
    }

    // Submit answers
    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable Long attemptId,
            @RequestBody QuizSubmissionRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                quizService.submitQuiz(
                        attemptId,
                        request,
                        authentication.getName()));
    }

    // View logged-in employee's results
    @GetMapping("/my-results")
    public ResponseEntity<List<QuizResultResponse>> getMyResults(
            Authentication authentication) {

        return ResponseEntity.ok(
                quizService.getMyResults(authentication.getName()));
    }

    // View one of the logged-in employee's results
    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<QuizResultResponse> getMyResultByAttemptId(
            @PathVariable Long attemptId,
            Authentication authentication) {

        return ResponseEntity.ok(
                quizService.getMyResultByAttemptId(
                        attemptId, authentication.getName()));
    }
}
