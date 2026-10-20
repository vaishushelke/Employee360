package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.backend.entities.TopicSlot;

public interface TopicSlotRepo extends JpaRepository<TopicSlot, Long> {

    List<TopicSlot> findAllByOrderBySlotNumberAsc();
}
