package com.employee360.backend.dto.response;

public record LearningTopicAdminResponse(
        Long id,
        Long departmentId,
        String departmentName,
        String title,
        String outline,
        int topicOrder,
        boolean archived) {
}
