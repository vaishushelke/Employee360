
package com.employee360.backend.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.employee360.backend.dto.request.SalaryPaymentRequest;
import com.employee360.backend.dto.response.SalaryPaymentResponse;
import com.employee360.backend.entities.Employee;
import com.employee360.backend.entities.SalaryPayment;
import com.employee360.backend.entities.User;
import com.employee360.backend.enums.PaymentStatus;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.repos.SalaryPaymentRepo;
import com.employee360.backend.repos.UserRepo;
import com.employee360.backend.services.SalaryPaymentService;

@Service
@Transactional
public class SalaryPaymentDao implements SalaryPaymentService {

    private final SalaryPaymentRepo salaryPaymentRepo;
        private final UserRepo userRepo;
        private final EmployeeRepo employeeRepo;

        public SalaryPaymentDao(
                        SalaryPaymentRepo salaryPaymentRepo,
                        UserRepo userRepo,
                        EmployeeRepo employeeRepo) {
        this.salaryPaymentRepo = salaryPaymentRepo;
                this.userRepo = userRepo;
                this.employeeRepo = employeeRepo;
    }

    // CREATE SALARY RECORD
    @Override
    public SalaryPaymentResponse createSalaryPayment(
            SalaryPaymentRequest request) {

        if (request == null
                || request.getEmployeeUsername() == null
                || request.getEmployeeUsername().isBlank()
                || request.getSalaryMonth() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Employee username and salary month are required");
        }

        String username = request.getEmployeeUsername().trim();
        LocalDate salaryMonth = request.getSalaryMonth();

        if (salaryMonth.getDayOfMonth() != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Salary month must use the first day of the month");
        }

        BigDecimal basic = validateAmount(
                request.getBasicSalary(), "Basic salary");

        BigDecimal allowances = validateAmount(
                request.getAllowances(), "Allowances");

        BigDecimal deductions = validateAmount(
                request.getDeductions(), "Deductions");

        BigDecimal netSalary = basic.add(allowances)
                .subtract(deductions)
                .setScale(2, RoundingMode.HALF_UP);

        if (netSalary.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Deductions cannot exceed basic salary plus allowances");
        }

        if (salaryPaymentRepo
                .findByEmployeeUsernameAndSalaryMonth(username, salaryMonth)
                .isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Salary record already exists for this employee and month");
        }

        SalaryPayment payment = new SalaryPayment();
        payment.setEmployeeUsername(username);
        payment.setSalaryMonth(salaryMonth);
        payment.setBasicSalary(basic);
        payment.setAllowances(allowances);
        payment.setDeductions(deductions);
        payment.setNetSalary(netSalary);
        payment.setCurrency("INR");
        payment.setPaymentStatus(PaymentStatus.PENDING);

        SalaryPayment saved = salaryPaymentRepo.save(payment);

        return toResponse(saved);
    }

    // GET ALL SALARY RECORDS
    @Override
    @Transactional(readOnly = true)
    public List<SalaryPaymentResponse> getAllSalaryPayments() {

        return salaryPaymentRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET EMPLOYEE SALARY HISTORY
    @Override
    @Transactional(readOnly = true)
    public List<SalaryPaymentResponse> getEmployeeSalaryHistory(
            String employeeUsername) {

        if (employeeUsername == null || employeeUsername.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Employee username is required");
        }

        return salaryPaymentRepo
                .findByEmployeeUsernameOrderBySalaryMonthDesc(
                        employeeUsername.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET SALARY RECORD BY ID
    @Override
    @Transactional(readOnly = true)
    public SalaryPaymentResponse getSalaryPaymentById(Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Salary payment ID is required");
        }

        SalaryPayment payment = salaryPaymentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Salary payment record not found"));

        return toResponse(payment);
    }

    // UPDATE PAYMENT STATUS
    @Override
    public SalaryPaymentResponse updatePaymentStatus(
            Long id,
            String paymentStatus,
            String transactionReference) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Salary payment ID is required");
        }

        SalaryPayment payment = salaryPaymentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Salary payment record not found"));

        PaymentStatus newStatus;

        try {
            newStatus = PaymentStatus.valueOf(
                    paymentStatus == null
                            ? ""
                            : paymentStatus.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be PROCESSING, PAID, or FAILED");
        }

        if (newStatus != PaymentStatus.PROCESSING
                && newStatus != PaymentStatus.PAID
                && newStatus != PaymentStatus.FAILED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be PROCESSING, PAID, or FAILED");
        }

        if (payment.getPaymentStatus() == PaymentStatus.PAID
                || payment.getPaymentStatus() == PaymentStatus.FAILED) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Finalized payment status cannot be changed");
        }

        if (newStatus == PaymentStatus.PAID
                && (transactionReference == null
                || transactionReference.isBlank())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction reference is required for PAID status");
        }

        if (transactionReference != null
                && transactionReference.length() > 150) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction reference cannot exceed 150 characters");
        }

        payment.setPaymentStatus(newStatus);

        if (transactionReference != null
                && !transactionReference.isBlank()) {

            payment.setTransactionReference(
                    transactionReference.trim());
        }

        if (newStatus == PaymentStatus.PAID) {
            payment.setPaymentDate(LocalDate.now());
        }

        return toResponse(salaryPaymentRepo.save(payment));
    }

    // VALIDATE MONEY AMOUNTS
    private BigDecimal validateAmount(
            BigDecimal amount, String fieldName) {

        if (amount == null || amount.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " must be zero or greater");
        }

        if (amount.scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " cannot have more than two decimal places");
        }

        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private SalaryPaymentResponse toResponse(SalaryPayment payment) {

        return new SalaryPaymentResponse(
                payment.getId(),
                payment.getEmployeeUsername(),
                employeeCodeFor(payment.getEmployeeUsername()),
                payment.getSalaryMonth(),
                payment.getBasicSalary(),
                payment.getAllowances(),
                payment.getDeductions(),
                payment.getNetSalary(),
                "INR",
                payment.getPaymentStatus().name(),
                payment.getPaymentDate(),
                payment.getTransactionReference(),
                payment.getCreatedAt());
    }

        private String employeeCodeFor(String username) {
                User user = userRepo.findByUsername(username).orElse(null);
                if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
                        return null;
                }
                Employee employee = employeeRepo.findByEmailIgnoreCase(user.getEmail());
                return employee == null ? null : employee.getEmployeeCode();
        }
}
