
package com.employee360.backend.services;

import java.util.List;

import com.employee360.backend.dto.request.ReimbursementRequest;
import com.employee360.backend.dto.response.ReimbursementResponse;

public interface ReimbursementService {

    // Employee submits a reimbursement claim
    ReimbursementResponse submitReimbursement(
            ReimbursementRequest request,
            String employeeUsername);

    // Employee views their own claims
    List<ReimbursementResponse> getMyReimbursements(
            String employeeUsername);

    // Authorized staff view all claims
    List<ReimbursementResponse> getAllReimbursements();

    // View one claim with access checks
    ReimbursementResponse getReimbursementById(
            Long id,
            String employeeUsername,
            boolean canViewAll);

    // HR or Manager approves or rejects a claim
    ReimbursementResponse reviewReimbursement(
            Long id,
            String approvalStatus,
            String reviewerUsername);

    // Authorized staff update the payment status
    ReimbursementResponse updatePaymentStatus(
            Long id,
            String paymentStatus,
            String transactionReference);
}
