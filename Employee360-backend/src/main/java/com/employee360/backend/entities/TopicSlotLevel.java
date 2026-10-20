package com.employee360.backend.entities;

import com.employee360.backend.enums.QuizDifficulty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "topic_slot_levels",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_topic_slot_difficulty",
                columnNames = {"topic_slot_id", "difficulty"}))
public class TopicSlotLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_slot_id", nullable = false)
    private TopicSlot topicSlot;

    @Enumerated(EnumType.STRING)
    private QuizDifficulty difficulty;

    private int plannedQuestionCount;

    public TopicSlotLevel() {
    }

    public TopicSlotLevel(
            TopicSlot topicSlot,
            QuizDifficulty difficulty,
            int plannedQuestionCount) {
        this.topicSlot = topicSlot;
        this.difficulty = difficulty;
        this.plannedQuestionCount = plannedQuestionCount;
    }

    public Long getId() {
        return id;
    }

    public TopicSlot getTopicSlot() {
        return topicSlot;
    }

    public QuizDifficulty getDifficulty() {
        return difficulty;
    }

    public int getPlannedQuestionCount() {
        return plannedQuestionCount;
    }
}
