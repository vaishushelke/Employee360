package com.employee360.backend.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.employee360.backend.entities.DepartmentTopic;

public interface DepartmentTopicRepo
        extends JpaRepository<DepartmentTopic, Long> {

    List<DepartmentTopic> findAllByOrderByTopicOrderAsc();

        @Query("select topic from DepartmentTopic topic join fetch topic.department "
            + "where topic.archived = false "
            + "order by topic.department.name asc, topic.topicOrder asc")
        List<DepartmentTopic> findActiveCatalogTopics();

        Optional<DepartmentTopic> findByIdAndArchivedFalse(Long id);

            boolean existsByDepartment_IdAndTitleIgnoreCaseAndArchivedFalse(Long departmentId, String title);

            boolean existsByDepartment_IdAndTitleIgnoreCaseAndArchivedFalseAndIdNot(
                Long departmentId, String title, Long id);

        @Query("select coalesce(max(topic.topicOrder), 0) from DepartmentTopic topic "
            + "where topic.department.id = :departmentId")
        int findMaxTopicOrder(@Param("departmentId") Long departmentId);
}
