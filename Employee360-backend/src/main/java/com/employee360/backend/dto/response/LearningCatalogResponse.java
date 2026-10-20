package com.employee360.backend.dto.response;

import java.util.List;

import com.employee360.backend.enums.QuizDifficulty;

public record LearningCatalogResponse(
        List<DepartmentCatalog> departments,
        int totalTopics,
        int totalSlots,
        int plannedQuestions) {

    public record DepartmentCatalog(
            Long id,
            String name,
            String description,
            List<TopicCatalog> topics) {
    }

    public record TopicCatalog(
            Long id,
            String title,
            String outline,
            int topicOrder,
            List<SlotCatalog> slots) {
    }

    public record SlotCatalog(
            Long id,
            int slotNumber,
            String title,
            List<LevelCatalog> levels) {
    }

    public record LevelCatalog(
            QuizDifficulty difficulty,
            int plannedQuestionCount) {
    }
}
