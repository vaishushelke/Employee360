
package com.employee360.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.employee360.backend.dto.request.SalaryPaymentRequest;
import com.employee360.backend.dto.response.SalaryPaymentResponse;
import com.employee360.backend.services.SalaryPaymentService;

@RestController
@RequestMapping("/api/salary-payments")
public class SalaryPaymentController {

    private final SalaryPaymentService salaryPaymentService;

    public SalaryPaymentController(
            SalaryPaymentService salaryPaymentService) {
        this.salaryPaymentService = salaryPaymentService;
    }

    // CREATE SALARY RECORD
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<SalaryPaymentResponse> createSalaryPayment(
            @RequestBody SalaryPaymentRequest request) {

        SalaryPaymentResponse response =
                salaryPaymentService.createSalaryPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET ALL SALARY RECORDS
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR')")
    public ResponseEntity<List<SalaryPaymentResponse>> getAllSalaryPayments() {

        return ResponseEntity.ok(
                salaryPaymentService.getAllSalaryPayments());
    }

    // GET MY SALARY HISTORY
    @GetMapping("/my")
    public ResponseEntity<List<SalaryPaymentResponse>> getMySalaryHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                salaryPaymentService.getEmployeeSalaryHistory(
                        authentication.getName()));
    }

    // GET SALARY HISTORY FOR A SPECIFIC EMPLOYEE
    @GetMapping("/employee/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR')")
    public ResponseEntity<List<SalaryPaymentResponse>>
            getEmployeeSalaryHistory(@PathVariable String username) {

        return ResponseEntity.ok(
                salaryPaymentService.getEmployeeSalaryHistory(username));
    }

    // GET SALARY RECORD BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR')")
    public ResponseEntity<SalaryPaymentResponse> getSalaryPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                salaryPaymentService.getSalaryPaymentById(id));
    }

    // UPDATE PAYMENT STATUS
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<SalaryPaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String transactionReference) {

        return ResponseEntity.ok(
                salaryPaymentService.updatePaymentStatus(
                        id, status, transactionReference));
    }
}
