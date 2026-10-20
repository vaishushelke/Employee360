
package com.employee360.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SalaryPaymentResponse {

    private Long id;
    private String employeeUsername;
    private String employeeCode;
    private LocalDate salaryMonth;
    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private String currency;
    private String paymentStatus;
    private LocalDate paymentDate;
    private String transactionReference;
    private LocalDateTime createdAt;

    public SalaryPaymentResponse() {
    }

    public SalaryPaymentResponse(
            Long id,
            String employeeUsername,
            String employeeCode,
            LocalDate salaryMonth,
            BigDecimal basicSalary,
            BigDecimal allowances,
            BigDecimal deductions,
            BigDecimal netSalary,
            String currency,
            String paymentStatus,
            LocalDate paymentDate,
            String transactionReference,
            LocalDateTime createdAt) {

        this.id = id;
        this.employeeUsername = employeeUsername;
        this.employeeCode = employeeCode;
        this.salaryMonth = salaryMonth;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.netSalary = netSalary;
        this.currency = currency;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.transactionReference = transactionReference;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getSalaryMonth() {
        return salaryMonth;
    }

    public void setSalaryMonth(LocalDate salaryMonth) {
        this.salaryMonth = salaryMonth;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        if (currency == null || !"INR".equals(currency)) {
            throw new IllegalArgumentException(
                    "Only Indian Rupees (INR) are supported");
        }

        this.currency = "INR";
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
