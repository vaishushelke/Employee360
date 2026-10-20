package com.employee360.backend.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.employee360.backend.dto.response.LearningCatalogResponse;
import com.employee360.backend.dto.response.LearningCatalogResponse.DepartmentCatalog;
import com.employee360.backend.dto.response.LearningCatalogResponse.LevelCatalog;
import com.employee360.backend.dto.response.LearningCatalogResponse.SlotCatalog;
import com.employee360.backend.dto.response.LearningCatalogResponse.TopicCatalog;
import com.employee360.backend.dto.request.LearningTopicRequest;
import com.employee360.backend.dto.response.LearningTopicAdminResponse;
import com.employee360.backend.entities.Department;
import com.employee360.backend.entities.DepartmentTopic;
import com.employee360.backend.entities.TopicSlot;
import com.employee360.backend.entities.TopicSlotLevel;
import com.employee360.backend.enums.QuizDifficulty;
import com.employee360.backend.exception.DuplicateResourceException;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.DepartmentTopicRepo;
import com.employee360.backend.repos.TopicSlotLevelRepo;
import com.employee360.backend.repos.TopicSlotRepo;
import com.employee360.backend.services.LearningCatalogService;

@Service
public class LearningCatalogServiceImpl implements LearningCatalogService {

    private final DepartmentTopicRepo topicRepo;
        private final DepartmentRepo departmentRepo;
    private final TopicSlotRepo slotRepo;
    private final TopicSlotLevelRepo levelRepo;

    public LearningCatalogServiceImpl(
            DepartmentRepo departmentRepo,
            DepartmentTopicRepo topicRepo,
            TopicSlotRepo slotRepo,
            TopicSlotLevelRepo levelRepo) {
        this.departmentRepo = departmentRepo;
        this.topicRepo = topicRepo;
        this.slotRepo = slotRepo;
        this.levelRepo = levelRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public LearningCatalogResponse getCatalog() {
        List<DepartmentTopic> topics = topicRepo.findActiveCatalogTopics();
        Set<Long> activeTopicIds = topics.stream()
                .map(DepartmentTopic::getId)
                .collect(Collectors.toSet());
        List<TopicSlot> slots = slotRepo.findAllByOrderBySlotNumberAsc().stream()
                .filter(slot -> activeTopicIds.contains(slot.getTopic().getId()))
                .toList();
        Set<Long> activeSlotIds = slots.stream()
                .map(TopicSlot::getId)
                .collect(Collectors.toSet());
        List<TopicSlotLevel> levels = levelRepo.findAll().stream()
                .filter(level -> activeSlotIds.contains(level.getTopicSlot().getId()))
                .toList();

        Map<Long, List<TopicSlotLevel>> levelsBySlot = levels.stream()
                .collect(Collectors.groupingBy(
                        level -> level.getTopicSlot().getId()));
        Map<Long, List<TopicSlot>> slotsByTopic = slots.stream()
                .collect(Collectors.groupingBy(
                        slot -> slot.getTopic().getId()));
        Map<Long, List<DepartmentTopic>> topicsByDepartment = topics.stream()
                .collect(Collectors.groupingBy(
                        topic -> topic.getDepartment().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<Department> allDepartments = departmentRepo.findAll();
        allDepartments.sort(Comparator.comparing(
                Department::getName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        List<DepartmentCatalog> departments = new ArrayList<>();
        for (Department department : allDepartments) {
            List<DepartmentTopic> departmentTopics = topicsByDepartment
                    .getOrDefault(department.getId(), new ArrayList<>());
            departmentTopics.sort(Comparator.comparingInt(DepartmentTopic::getTopicOrder));
            List<TopicCatalog> topicCatalogs = departmentTopics.stream()
                    .map(topic -> {
                        List<SlotCatalog> slotCatalogs = slotsByTopic
                                .getOrDefault(topic.getId(), List.of())
                                .stream()
                                .sorted(Comparator.comparingInt(TopicSlot::getSlotNumber))
                                .map(slot -> new SlotCatalog(
                                        slot.getId(),
                                        slot.getSlotNumber(),
                                        slot.getTitle(),
                                        levelsBySlot.getOrDefault(slot.getId(), List.of())
                                                .stream()
                                                .sorted(Comparator.comparing(
                                                        TopicSlotLevel::getDifficulty))
                                                .map(level -> new LevelCatalog(
                                                        level.getDifficulty(),
                                                        level.getPlannedQuestionCount()))
                                                .toList()))
                                .toList();
                        return new TopicCatalog(
                                topic.getId(),
                                topic.getTitle(),
                                topic.getOutline(),
                                topic.getTopicOrder(),
                                slotCatalogs);
                    })
                    .toList();
            departments.add(new DepartmentCatalog(
                    department.getId(),
                    department.getName(),
                    department.getDescription(),
                    topicCatalogs));
        }

        departments.sort(Comparator.comparing(DepartmentCatalog::name));
        int totalSlots = slots.size();
        int plannedQuestions = levels.stream()
                .mapToInt(TopicSlotLevel::getPlannedQuestionCount)
                .sum();

        return new LearningCatalogResponse(
                departments,
                topics.size(),
                totalSlots,
                plannedQuestions);
    }

    @Override
        @Transactional
    public LearningTopicAdminResponse createTopic(LearningTopicRequest request) {
        Department department = getDepartment(request.getDepartmentId());
        String title = request.getTitle().trim();
        ensureUniqueTitle(department.getId(), title, null);

        DepartmentTopic topic = new DepartmentTopic(
                department,
                title,
                request.getOutline().trim(),
                topicRepo.findMaxTopicOrder(department.getId()) + 1);
        DepartmentTopic savedTopic = topicRepo.save(topic);

        List<TopicSlot> slots = java.util.stream.IntStream.rangeClosed(1, 10)
                .mapToObj(slotNumber -> new TopicSlot(
                        savedTopic,
                        slotNumber,
                        String.format(java.util.Locale.ROOT, "Slot %02d", slotNumber)))
                .toList();
        slots = slotRepo.saveAllAndFlush(slots);

        List<TopicSlotLevel> levels = slots.stream()
                .flatMap(slot -> java.util.Arrays.stream(QuizDifficulty.values())
                        .map(difficulty -> new TopicSlotLevel(slot, difficulty, 20)))
                .toList();
        levelRepo.saveAll(levels);

        return toAdminResponse(savedTopic);
    }

    @Override
        @Transactional
    public LearningTopicAdminResponse updateTopic(
            Long topicId,
            LearningTopicRequest request) {
        DepartmentTopic topic = topicRepo.findByIdAndArchivedFalse(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning topic not found"));
        Department department = getDepartment(request.getDepartmentId());
        String title = request.getTitle().trim();
        ensureUniqueTitle(department.getId(), title, topicId);

        if (!topic.getDepartment().getId().equals(department.getId())) {
            topic.setTopicOrder(topicRepo.findMaxTopicOrder(department.getId()) + 1);
            topic.setDepartment(department);
        }
        topic.setTitle(title);
        topic.setOutline(request.getOutline().trim());
        return toAdminResponse(topicRepo.save(topic));
    }

    @Override
        @Transactional
    public void archiveTopic(Long topicId) {
        DepartmentTopic topic = topicRepo.findByIdAndArchivedFalse(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning topic not found"));
        topic.setArchived(true);
        topicRepo.save(topic);
    }

    private Department getDepartment(Long departmentId) {
        return departmentRepo.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    private void ensureUniqueTitle(Long departmentId, String title, Long topicId) {
        boolean exists = topicId == null
                ? topicRepo.existsByDepartment_IdAndTitleIgnoreCaseAndArchivedFalse(departmentId, title)
                : topicRepo.existsByDepartment_IdAndTitleIgnoreCaseAndArchivedFalseAndIdNot(
                        departmentId, title, topicId);
        if (exists) {
            throw new DuplicateResourceException("A learning topic with this title already exists in the department");
        }
    }

    private LearningTopicAdminResponse toAdminResponse(DepartmentTopic topic) {
        return new LearningTopicAdminResponse(
                topic.getId(),
                topic.getDepartment().getId(),
                topic.getDepartment().getName(),
                topic.getTitle(),
                topic.getOutline(),
                topic.getTopicOrder(),
                topic.isArchived());
    }
}
