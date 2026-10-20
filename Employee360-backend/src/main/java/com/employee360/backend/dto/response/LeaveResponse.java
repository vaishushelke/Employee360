package com.employee360.backend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.employee360.backend.entities.Employee;
import com.employee360.backend.entities.Leave;
import com.employee360.backend.enums.LeaveStatus;

public record LeaveResponse(
        Long id,
        String leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        LeaveStatus status,
        EmployeeSummary employee,
        String reviewedBy,
        LocalDateTime reviewedAt,
        String reviewComment) {

    public static LeaveResponse from(Leave leave) {
        Employee employee = leave.getEmployee();
        EmployeeSummary employeeSummary = employee == null ? null
                : new EmployeeSummary(
                        employee.getId(),
                        employee.getEmployeeCode(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getEmail());

        return new LeaveResponse(
                leave.getId(),
                leave.getLeaveType(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getReason(),
                leave.getStatus(),
                employeeSummary,
                leave.getReviewedBy(),
                leave.getReviewedAt(),
                leave.getReviewComment());
    }

    public record EmployeeSummary(
            Long id,
            String employeeCode,
            String firstName,
            String lastName,
            String email) {
    }
}
