package com.employee360.backend.dto.request;

import jakarta.validation.constraints.Size;

public class LeaveReviewRequest {

    @Size(max = 1000, message = "Review comment cannot exceed 1000 characters")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
