package com.employee360.backend.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.employee360.backend.dto.request.LearningTopicRequest;
import com.employee360.backend.entities.Department;
import com.employee360.backend.entities.DepartmentTopic;
import com.employee360.backend.entities.TopicSlot;
import com.employee360.backend.entities.TopicSlotLevel;
import com.employee360.backend.exception.DuplicateResourceException;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.DepartmentTopicRepo;
import com.employee360.backend.repos.TopicSlotLevelRepo;
import com.employee360.backend.repos.TopicSlotRepo;

@ExtendWith(MockitoExtension.class)
class LearningCatalogServiceImplTest {

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private DepartmentTopicRepo topicRepo;

    @Mock
    private TopicSlotRepo slotRepo;

    @Mock
    private TopicSlotLevelRepo levelRepo;

    @InjectMocks
    private LearningCatalogServiceImpl service;

    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(7L);
        department.setName("Information Technology");
        department.setDescription("Technology team");
    }

    @Test
    void createTopicPersistsTopicAndItsPlannedLearningStructure() {
        LearningTopicRequest request = request("Network Security", "Network security basics");
        when(departmentRepo.findById(7L)).thenReturn(Optional.of(department));
        when(topicRepo.findMaxTopicOrder(7L)).thenReturn(4);
        when(topicRepo.save(any(DepartmentTopic.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(slotRepo.saveAllAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.createTopic(request);

        assertEquals("Network Security", response.title());
        assertEquals("Information Technology", response.departmentName());
        assertEquals(5, response.topicOrder());
        verify(slotRepo).saveAllAndFlush(argThat(slots ->
                StreamSupport.stream(slots.spliterator(), false).count() == 10));
        verify(levelRepo).saveAll(argThat(levels ->
                StreamSupport.stream(levels.spliterator(), false).count() == 50));
    }

    @Test
    void createTopicRejectsDuplicateTitleWithinDepartment() {
        LearningTopicRequest request = request("Network Security", "Description");
        when(departmentRepo.findById(7L)).thenReturn(Optional.of(department));
        when(topicRepo.existsByDepartment_IdAndTitleIgnoreCaseAndArchivedFalse(7L, "Network Security"))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createTopic(request));
        verify(topicRepo, never()).save(any(DepartmentTopic.class));
    }

    @Test
    void updateTopicUpdatesTitleAndOutlineWithoutChangingOrder() {
        DepartmentTopic topic = new DepartmentTopic(department, "Old title", "Old outline", 3);
        LearningTopicRequest request = request("Updated title", "Updated outline");
        when(topicRepo.findByIdAndArchivedFalse(12L)).thenReturn(Optional.of(topic));
        when(departmentRepo.findById(7L)).thenReturn(Optional.of(department));
        when(topicRepo.existsByDepartment_IdAndTitleIgnoreCaseAndArchivedFalseAndIdNot(
                7L, "Updated title", 12L)).thenReturn(false);
        when(topicRepo.save(topic)).thenReturn(topic);

        var response = service.updateTopic(12L, request);

        assertEquals("Updated title", response.title());
        assertEquals("Updated outline", response.outline());
        assertEquals(3, response.topicOrder());
    }

    @Test
    void archiveTopicRetainsTopicRecordAndDoesNotDeleteSlots() {
        DepartmentTopic topic = new DepartmentTopic(department, "Archived topic", "Outline", 2);
        when(topicRepo.findByIdAndArchivedFalse(12L)).thenReturn(Optional.of(topic));
        when(topicRepo.save(topic)).thenReturn(topic);

        service.archiveTopic(12L);

        assertTrue(topic.isArchived());
        verify(topicRepo).save(topic);
        verify(slotRepo, never()).deleteAll(any());
        verify(levelRepo, never()).deleteAll(any());
    }

    private LearningTopicRequest request(String title, String outline) {
        LearningTopicRequest request = new LearningTopicRequest();
        request.setDepartmentId(7L);
        request.setTitle(title);
        request.setOutline(outline);
        return request;
    }
}
