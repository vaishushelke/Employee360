
package com.employee360.backend.services;

import java.util.List;

import com.employee360.backend.dto.request.QuizCreateRequest;
import com.employee360.backend.dto.request.QuizQuestionRequest;
import com.employee360.backend.dto.request.QuizSubmissionRequest;
import com.employee360.backend.dto.response.QuizQuestionResponse;
import com.employee360.backend.dto.response.QuizResponse;
import com.employee360.backend.dto.response.QuizResultResponse;

public interface QuizService {

    QuizResponse createQuiz(QuizCreateRequest request);

    List<QuizResponse> getAllQuizzesForAdmin();

    QuizResponse updateQuiz(Long quizId, QuizCreateRequest request);

    void archiveQuiz(Long quizId);

    List<QuizResponse> getActiveQuizzes();

    QuizResponse getQuizById(Long quizId);

    QuizQuestionResponse addQuestion(
            Long quizId,
            QuizQuestionRequest request
    );

    List<QuizQuestionResponse> getQuizQuestions(Long quizId);

    List<QuizQuestionResponse> getQuizQuestionsForAdmin(Long quizId);

    QuizResultResponse startQuiz(Long quizId, String username);

    QuizResultResponse submitQuiz(
            Long attemptId,
            QuizSubmissionRequest request,
            String username
    );

    List<QuizResultResponse> getMyResults(String username);

    QuizResultResponse getMyResultByAttemptId(
            Long attemptId,
            String username
    );
}
