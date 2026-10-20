package com.employee360.backend.dto.request;

import java.time.LocalDate;

import com.employee360.backend.enums.AttendanceStatus;

import jakarta.validation.constraints.NotNull;

public class AttendanceRequest {

    private Long employeeId;
    private String employeeCode;
    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;
    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;
    private String checkInTime;
    private String checkOutTime;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
