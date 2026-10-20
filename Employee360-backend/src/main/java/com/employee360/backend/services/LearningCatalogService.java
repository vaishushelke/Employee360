package com.employee360.backend.services;

import com.employee360.backend.dto.request.LearningTopicRequest;
import com.employee360.backend.dto.response.LearningCatalogResponse;
import com.employee360.backend.dto.response.LearningTopicAdminResponse;

public interface LearningCatalogService {

    LearningCatalogResponse getCatalog();

    LearningTopicAdminResponse createTopic(LearningTopicRequest request);

    LearningTopicAdminResponse updateTopic(Long topicId, LearningTopicRequest request);

    void archiveTopic(Long topicId);
}
