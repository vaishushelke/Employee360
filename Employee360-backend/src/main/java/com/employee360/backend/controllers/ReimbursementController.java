
package com.employee360.backend.controllers;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.employee360.backend.dto.request.ReimbursementRequest;
import com.employee360.backend.dto.response.ReimbursementResponse;
import com.employee360.backend.services.ReimbursementService;

@RestController
@RequestMapping("/api/reimbursements")
public class ReimbursementController {

    private final ReimbursementService reimbursementService;

    public ReimbursementController(
            ReimbursementService reimbursementService) {
        this.reimbursementService = reimbursementService;
    }

    // EMPLOYEE SUBMITS A CLAIM
    @PostMapping
    public ReimbursementResponse submitReimbursement(
    		 @Valid   @RequestBody ReimbursementRequest request,
            Authentication authentication) {

        return reimbursementService.submitReimbursement(
                request,
                authentication.getName());
    }

    // EMPLOYEE VIEWS THEIR OWN CLAIMS
    @GetMapping("/my")
    public List<ReimbursementResponse> getMyReimbursements(
            Authentication authentication) {

        return reimbursementService.getMyReimbursements(
                authentication.getName());
    }

    // ADMIN, CEO, HR OR MANAGER VIEWS ALL CLAIMS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR', 'MANAGER')")
    public List<ReimbursementResponse> getAllReimbursements() {

        return reimbursementService.getAllReimbursements();
    }

    // VIEW ONE CLAIM
    @GetMapping("/{id}")
    public ReimbursementResponse getReimbursementById(
    		 @Valid @PathVariable Long id,
            Authentication authentication) {

        boolean canViewAll = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                        || authority.getAuthority().equals("ROLE_CEO")
                        || authority.getAuthority().equals("ROLE_HR")
                        || authority.getAuthority().equals("ROLE_MANAGER"));

        return reimbursementService.getReimbursementById(
                id,
                authentication.getName(),
                canViewAll);
    }

    // HR OR MANAGER APPROVES OR REJECTS A CLAIM
    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ReimbursementResponse reviewReimbursement(
    		 @Valid @PathVariable Long id,
            @RequestParam String approvalStatus,
            Authentication authentication) {

        return reimbursementService.reviewReimbursement(
                id,
                approvalStatus,
                authentication.getName());
    }

    // AUTHORIZED STAFF RECORD PAYMENT STATUS
    @PatchMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ReimbursementResponse updatePaymentStatus(
    		 @Valid  @PathVariable Long id,
            @RequestParam String paymentStatus,
            @RequestParam(required = false) String transactionReference) {

        return reimbursementService.updatePaymentStatus(
                id,
                paymentStatus,
                transactionReference);
    }
}
