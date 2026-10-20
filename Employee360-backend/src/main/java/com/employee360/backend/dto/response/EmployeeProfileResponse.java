package com.employee360.backend.dto.response;

import java.time.LocalDate;

import com.employee360.backend.entities.Employee;

public record EmployeeProfileResponse(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String designation,
        LocalDate joiningDate,
        String status,
        String departmentName) {

    public static EmployeeProfileResponse from(Employee employee) {
        return new EmployeeProfileResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDesignation(),
                employee.getJoiningDate(),
                employee.getStatus(),
                employee.getDepartment() == null ? null : employee.getDepartment().getName());
    }
}
