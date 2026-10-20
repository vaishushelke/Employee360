
package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Reimbursement;
import com.employee360.backend.enums.PaymentStatus;
import com.employee360.backend.enums.ReimbursementStatus;

@Repository
public interface ReimbursementRepo
        extends JpaRepository<Reimbursement, Long> {

    // Get all reimbursement claims submitted by an employee
    List<Reimbursement> findByEmployeeUsernameOrderByCreatedAtDesc(
            String employeeUsername);

    // Get claims by approval status
    List<Reimbursement> findByApprovalStatusOrderByCreatedAtDesc(
            ReimbursementStatus approvalStatus);

    // Get claims by payment status
    List<Reimbursement> findByPaymentStatusOrderByCreatedAtDesc(
            PaymentStatus paymentStatus);

    // Get claims for an employee filtered by approval status
    List<Reimbursement> findByEmployeeUsernameAndApprovalStatusOrderByCreatedAtDesc(
            String employeeUsername,
            ReimbursementStatus approvalStatus);
    
    long countByApprovalStatus(ReimbursementStatus approvalStatus);

    long countByPaymentStatus(PaymentStatus paymentStatus);
}
