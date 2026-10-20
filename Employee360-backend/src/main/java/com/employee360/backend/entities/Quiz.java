
package com.employee360.backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private int totalMarks;

    private int durationMinutes;

    private boolean active = true;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean archived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private DepartmentTopic topic;

    @Enumerated(EnumType.STRING)
    private com.employee360.backend.enums.QuizDifficulty difficulty;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Quiz() {
    }

    public Quiz(String title, String description,
                int totalMarks, int durationMinutes) {
        this.title = title;
        this.description = description;
        this.totalMarks = totalMarks;
        this.durationMinutes = durationMinutes;
        this.active = true;
        this.createdAt = LocalDateTime.now();
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public DepartmentTopic getTopic() {
        return topic;
    }

    public void setTopic(DepartmentTopic topic) {
        this.topic = topic;
    }

    public com.employee360.backend.enums.QuizDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(com.employee360.backend.enums.QuizDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
