
package com.employee360.backend.dto.response;

import java.time.LocalDateTime;

public class QuizResponse {

    private Long id;
    private String title;
    private String description;
    private int totalMarks;
    private int durationMinutes;
    private boolean active;
    private LocalDateTime createdAt;
    private Long topicId;
    private String topicTitle;
    private Long departmentId;
    private String departmentName;
    private String difficulty;
    private int questionCount;
    private boolean archived;

    public QuizResponse() {
    }

    public QuizResponse(
            Long id,
            String title,
            String description,
            int totalMarks,
            int durationMinutes,
            boolean active,
            LocalDateTime createdAt,
            Long topicId,
            String topicTitle,
            Long departmentId,
            String departmentName,
            String difficulty,
            int questionCount,
            boolean archived) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.totalMarks = totalMarks;
        this.durationMinutes = durationMinutes;
        this.active = active;
        this.createdAt = createdAt;
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.archived = archived;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
