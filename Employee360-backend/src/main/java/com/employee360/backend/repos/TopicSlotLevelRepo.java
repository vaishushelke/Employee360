package com.employee360.backend.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.backend.entities.TopicSlotLevel;

public interface TopicSlotLevelRepo
        extends JpaRepository<TopicSlotLevel, Long> {
}
