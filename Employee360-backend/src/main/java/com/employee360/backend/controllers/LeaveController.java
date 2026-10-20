package com.employee360.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee360.backend.dto.request.LeaveRequest;
import com.employee360.backend.dto.request.LeaveReviewRequest;
import com.employee360.backend.dto.response.LeaveApprovalHistoryResponse;
import com.employee360.backend.dto.response.LeaveResponse;
import com.employee360.backend.services.LeaveSer;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/Leave")
public class LeaveController {

    private final LeaveSer leaveService;

    public LeaveController(LeaveSer leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LeaveResponse> applyForCurrentUser(
            @Valid @RequestBody LeaveRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(leaveService.applyForCurrentUser(request, authentication.getName()));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LeaveResponse>> getMyLeaves(Authentication authentication) {
        return ResponseEntity.ok(leaveService.getMyLeaves(authentication.getName()));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR', 'MANAGER')")
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR', 'MANAGER')")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @GetMapping("/history/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LeaveApprovalHistoryResponse>> getHistory(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(leaveService.getHistory(id, authentication.getName()));
    }

    @PostMapping("/apply/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<LeaveResponse> applyForEmployee(
            @Valid @RequestBody LeaveRequest request,
            @PathVariable Long employeeId,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(leaveService.applyForEmployee(
                        request, employeeId, authentication.getName()));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<LeaveResponse> updatePendingLeave(
            @Valid @RequestBody LeaveRequest request,
            @PathVariable Long id) {
        return ResponseEntity.ok(leaveService.updatePendingLeave(request, id));
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) LeaveReviewRequest request,
            Authentication authentication) {
        String comment = request == null ? null : request.getComment();
        return ResponseEntity.ok(
                leaveService.approveLeave(id, authentication.getName(), comment));
    }

    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) LeaveReviewRequest request,
            Authentication authentication) {
        String comment = request == null ? null : request.getComment();
        return ResponseEntity.ok(
                leaveService.rejectLeave(id, authentication.getName(), comment));
    }


    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelMyLeave(
            @PathVariable Long id,
            Authentication authentication) {
        leaveService.cancelMyLeave(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
