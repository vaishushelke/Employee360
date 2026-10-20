package com.employee360.backend.entities;

import jakarta.persistence.Entity;
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
        name = "topic_slots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_topic_slot_number",
                columnNames = {"topic_id", "slot_number"}))
public class TopicSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private DepartmentTopic topic;

    private int slotNumber;

    private String title;

    public TopicSlot() {
    }

    public TopicSlot(DepartmentTopic topic, int slotNumber, String title) {
        this.topic = topic;
        this.slotNumber = slotNumber;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public DepartmentTopic getTopic() {
        return topic;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getTitle() {
        return title;
    }
    
}
