package com.employee360.backend.dto.response;

import java.time.LocalDate;

import com.employee360.backend.entities.Attendance;

public record AttendanceResponse(
        Long id,
        Long employeeId,
        String employeeCode,
        String employeeName,
        LocalDate attendanceDate,
        String status,
        String checkInTime,
        String checkOutTime) {

    public static AttendanceResponse from(Attendance attendance) {
        var employee = attendance.getEmployee();
        return new AttendanceResponse(
                attendance.getId(),
                employee == null ? null : employee.getId(),
                employee == null ? null : employee.getEmployeeCode(),
                employee == null ? null
                        : (employee.getFirstName() + " " + employee.getLastName()).trim(),
                attendance.getAttendanceDate(),
                attendance.getStatus() == null ? null : attendance.getStatus().name(),
                attendance.getCheckInTime(),
                attendance.getCheckOutTime());
    }
}
