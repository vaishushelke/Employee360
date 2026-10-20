
package com.employee360.backend.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.employee360.backend.dto.request.ReimbursementRequest;
import com.employee360.backend.dto.response.ReimbursementResponse;
import com.employee360.backend.entities.Reimbursement;
import com.employee360.backend.enums.PaymentStatus;
import com.employee360.backend.enums.ReimbursementStatus;
import com.employee360.backend.repos.ReimbursementRepo;
import com.employee360.backend.services.ReimbursementService;

@Service
@Transactional
public class ReimbursementDao implements ReimbursementService {

    private final ReimbursementRepo reimbursementRepo;

    public ReimbursementDao(ReimbursementRepo reimbursementRepo) {
        this.reimbursementRepo = reimbursementRepo;
    }

    // EMPLOYEE SUBMITS A CLAIM
    @Override
    public ReimbursementResponse submitReimbursement(
            ReimbursementRequest request,
            String employeeUsername) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reimbursement details are required.");
        }

        if (employeeUsername == null || employeeUsername.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Employee must be logged in.");
        }

        if (request.getExpenseTitle() == null
                || request.getExpenseTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expense title is required.");
        }

        if (request.getExpenseCategory() == null
                || request.getExpenseCategory().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expense category is required.");
        }

        BigDecimal amount = request.getAmount();

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expense amount must be greater than zero INR.");
        }

        if (amount.scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Amount can contain at most two decimal places.");
        }

        if (amount.precision() > 12) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expense amount is too large.");
        }

        if (request.getExpenseDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expense date is required.");
        }

        if (request.getExpenseDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expense date cannot be in the future.");
        }

        Reimbursement reimbursement = new Reimbursement();

        reimbursement.setEmployeeUsername(employeeUsername.trim());
        reimbursement.setExpenseTitle(request.getExpenseTitle().trim());
        reimbursement.setExpenseCategory(
                request.getExpenseCategory().trim());
        reimbursement.setDescription(request.getDescription());
        reimbursement.setAmount(amount);
        reimbursement.setCurrency("INR");
        reimbursement.setExpenseDate(request.getExpenseDate());

        reimbursement.setApprovalStatus(ReimbursementStatus.PENDING);
        reimbursement.setPaymentStatus(PaymentStatus.NOT_PAID);

        Reimbursement saved = reimbursementRepo.save(reimbursement);

        return convertToResponse(saved);
    }

    // EMPLOYEE VIEWS THEIR OWN CLAIMS
    @Override
    @Transactional(readOnly = true)
    public List<ReimbursementResponse> getMyReimbursements(
            String employeeUsername) {

        validateUsername(employeeUsername);

        return reimbursementRepo
                .findByEmployeeUsernameOrderByCreatedAtDesc(
                        employeeUsername.trim())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // AUTHORIZED STAFF VIEW ALL CLAIMS
    @Override
    @Transactional(readOnly = true)
    public List<ReimbursementResponse> getAllReimbursements() {

        return reimbursementRepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET ONE CLAIM WITH ACCESS CHECK
    @Override
    @Transactional(readOnly = true)
    public ReimbursementResponse getReimbursementById(
            Long id,
            String employeeUsername,
            boolean canViewAll) {

        Reimbursement reimbursement = findReimbursement(id);

        validateUsername(employeeUsername);

        if (!canViewAll
                && !reimbursement.getEmployeeUsername()
                        .equals(employeeUsername.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another employee's claim.");
        }

        return convertToResponse(reimbursement);
    }

    // HR OR MANAGER REVIEWS A CLAIM
    @Override
    public ReimbursementResponse reviewReimbursement(
            Long id,
            String approvalStatus,
            String reviewerUsername) {

        Reimbursement reimbursement = findReimbursement(id);

        if (reviewerUsername == null || reviewerUsername.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Reviewer must be logged in.");
        }

        if (reimbursement.getApprovalStatus()
                != ReimbursementStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This reimbursement has already been reviewed.");
        }

        ReimbursementStatus status;

        try {
            status = ReimbursementStatus.valueOf(
                    approvalStatus == null
                            ? ""
                            : approvalStatus.trim()
                                    .toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Approval status must be APPROVED or REJECTED.");
        }

        if (status != ReimbursementStatus.APPROVED
                && status != ReimbursementStatus.REJECTED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Approval status must be APPROVED or REJECTED.");
        }

        reimbursement.setApprovalStatus(status);
        reimbursement.setReviewedBy(reviewerUsername.trim());
        reimbursement.setReviewedAt(LocalDateTime.now());

        if (status == ReimbursementStatus.REJECTED) {
            reimbursement.setPaymentStatus(PaymentStatus.NOT_PAID);
        }

        Reimbursement saved = reimbursementRepo.save(reimbursement);

        return convertToResponse(saved);
    }

    // AUTHORIZED STAFF RECORD PAYMENT STATUS
    @Override
    public ReimbursementResponse updatePaymentStatus(
            Long id,
            String paymentStatus,
            String transactionReference) {

        Reimbursement reimbursement = findReimbursement(id);

        if (reimbursement.getApprovalStatus()
                != ReimbursementStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only approved claims can be paid.");
        }

        PaymentStatus status;

        try {
            status = PaymentStatus.valueOf(
                    paymentStatus == null
                            ? ""
                            : paymentStatus.trim()
                                    .toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment status must be PROCESSING, PAID or FAILED.");
        }

        if (status != PaymentStatus.PROCESSING
                && status != PaymentStatus.PAID
                && status != PaymentStatus.FAILED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment status must be PROCESSING, PAID or FAILED.");
        }

        if (reimbursement.getPaymentStatus() == PaymentStatus.PAID
                || reimbursement.getPaymentStatus() == PaymentStatus.FAILED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Finalized reimbursement payment status cannot be changed.");
        }

        if (status == PaymentStatus.PAID
                && (transactionReference == null
                || transactionReference.isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction reference is required for paid claims.");
        }

        if (transactionReference != null
                && transactionReference.length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction reference cannot exceed 150 characters.");
        }

        reimbursement.setPaymentStatus(status);

        if (status == PaymentStatus.PAID) {
            reimbursement.setTransactionReference(
                    transactionReference.trim());
            reimbursement.setPaymentDate(LocalDate.now());
        }

        Reimbursement saved = reimbursementRepo.save(reimbursement);

        return convertToResponse(saved);
    }

    // FIND CLAIM OR RETURN 404
    private Reimbursement findReimbursement(Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reimbursement ID is required.");
        }

        return reimbursementRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reimbursement not found."));
    }

    // VALIDATE EMPLOYEE USERNAME
    private void validateUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Employee must be logged in.");
        }
    }

    // ENTITY TO RESPONSE DTO
    private ReimbursementResponse convertToResponse(
            Reimbursement reimbursement) {

        ReimbursementResponse response = new ReimbursementResponse();

        response.setId(reimbursement.getId());
        response.setEmployeeUsername(
                reimbursement.getEmployeeUsername());
        response.setExpenseTitle(reimbursement.getExpenseTitle());
        response.setExpenseCategory(reimbursement.getExpenseCategory());
        response.setDescription(reimbursement.getDescription());
        response.setAmount(reimbursement.getAmount());
        response.setCurrency("INR");
        response.setExpenseDate(reimbursement.getExpenseDate());

        response.setApprovalStatus(
                reimbursement.getApprovalStatus() == null
                        ? null
                        : reimbursement.getApprovalStatus().name());

        response.setPaymentStatus(
                reimbursement.getPaymentStatus() == null
                        ? null
                        : reimbursement.getPaymentStatus().name());

        response.setReviewedBy(reimbursement.getReviewedBy());
        response.setReviewedAt(reimbursement.getReviewedAt());
        response.setPaymentDate(reimbursement.getPaymentDate());
        response.setTransactionReference(
                reimbursement.getTransactionReference());
        response.setCreatedAt(reimbursement.getCreatedAt());

        return response;
    }
}
