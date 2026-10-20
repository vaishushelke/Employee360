
package com.employee360.backend.services;

import java.util.List;

import com.employee360.backend.dto.request.SalaryPaymentRequest;
import com.employee360.backend.dto.response.SalaryPaymentResponse;

public interface SalaryPaymentService {

    // Create a salary record
    SalaryPaymentResponse createSalaryPayment(
            SalaryPaymentRequest request);

    // Get all salary records (authorized HR/Finance/Admin access)
    List<SalaryPaymentResponse> getAllSalaryPayments();

    // Get salary history for one employee
    List<SalaryPaymentResponse> getEmployeeSalaryHistory(
            String employeeUsername);

    // Get a salary record by ID
    SalaryPaymentResponse getSalaryPaymentById(Long id);

    // Update salary payment status
    SalaryPaymentResponse updatePaymentStatus(
            Long id,
            String paymentStatus,
            String transactionReference);
}