package com.employee360.backend.entities;

import java.time.LocalDateTime;

import com.employee360.backend.enums.LeaveStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leave_approval_history")
public class LeaveApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "leave_id", nullable = false)
    private Leave leave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveStatus decision;

    @Column(nullable = false, length = 100)
    private String reviewerUsername;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime decidedAt = LocalDateTime.now();

    public LeaveApprovalHistory() {
    }

    public LeaveApprovalHistory(
            Leave leave,
            LeaveStatus decision,
            String reviewerUsername,
            String comment,
            LocalDateTime decidedAt) {
        this.leave = leave;
        this.decision = decision;
        this.reviewerUsername = reviewerUsername;
        this.comment = comment;
        this.decidedAt = decidedAt;
    }

    public Long getId() {
        return id;
    }

    public Leave getLeave() {
        return leave;
    }

    public LeaveStatus getDecision() {
        return decision;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }
}
