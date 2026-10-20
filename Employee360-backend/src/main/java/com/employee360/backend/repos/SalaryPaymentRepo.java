
package com.employee360.backend.repos;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.SalaryPayment;
import com.employee360.backend.enums.PaymentStatus;

@Repository
public interface SalaryPaymentRepo
        extends JpaRepository<SalaryPayment, Long> {

    // Get all salary records for an employee
    List<SalaryPayment> findByEmployeeUsernameOrderBySalaryMonthDesc(
            String employeeUsername);

    // Find an employee's salary for a particular month
    Optional<SalaryPayment> findByEmployeeUsernameAndSalaryMonth(
            String employeeUsername,
            LocalDate salaryMonth);

    // Get salary records by payment status
    List<SalaryPayment> findByPaymentStatusOrderBySalaryMonthDesc(
            PaymentStatus paymentStatus);
    
    long countByPaymentStatus(PaymentStatus paymentStatus);
}
