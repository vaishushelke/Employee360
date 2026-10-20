
package com.employee360.backend.dto.response;

import java.time.LocalDateTime;

public class QuizResultResponse {

    private Long attemptId;
    private Long quizId;
    private String quizTitle;
    private int score;
    private int totalMarks;
    private double percentage;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    public QuizResultResponse() {
    }

    public QuizResultResponse(
            Long attemptId,
            Long quizId,
            String quizTitle,
            int score,
            int totalMarks,
            double percentage,
            String status,
            LocalDateTime startedAt,
            LocalDateTime submittedAt) {

        this.attemptId = attemptId;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.status = status;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
