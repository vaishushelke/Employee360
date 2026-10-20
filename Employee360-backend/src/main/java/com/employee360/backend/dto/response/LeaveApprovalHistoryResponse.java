package com.employee360.backend.dto.response;

import java.time.LocalDateTime;

import com.employee360.backend.entities.LeaveApprovalHistory;
import com.employee360.backend.enums.LeaveStatus;

public record LeaveApprovalHistoryResponse(
        Long id,
        LeaveStatus decision,
        String reviewerUsername,
        String comment,
        LocalDateTime decidedAt) {

    public static LeaveApprovalHistoryResponse from(LeaveApprovalHistory history) {
        return new LeaveApprovalHistoryResponse(
                history.getId(),
                history.getDecision(),
                history.getReviewerUsername(),
                history.getComment(),
                history.getDecidedAt());
    }
}
