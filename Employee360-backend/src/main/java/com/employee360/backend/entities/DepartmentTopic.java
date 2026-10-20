package com.employee360.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "department_topics",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_department_topic_order",
                columnNames = {"department_id", "topic_order"}))
public class DepartmentTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    private String title;

    private String outline;

    private int topicOrder;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean archived;

    public DepartmentTopic() {
    }

    public DepartmentTopic(
            Department department,
            String title,
            String outline,
            int topicOrder) {
        this.department = department;
        this.title = title;
        this.outline = outline;
        this.topicOrder = topicOrder;
    }

    public Long getId() {
        return id;
    }

    public Department getDepartment() {
        return department;
    }

    public String getTitle() {
        return title;
    }

    public String getOutline() {
        return outline;
    }

    public int getTopicOrder() {
        return topicOrder;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public void setTopicOrder(int topicOrder) {
        this.topicOrder = topicOrder;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
