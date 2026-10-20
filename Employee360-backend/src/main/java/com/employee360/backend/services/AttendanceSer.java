
package com.employee360.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.employee360.backend.dto.request.AttendanceRequest;
import com.employee360.backend.dto.response.AttendanceResponse;
import com.employee360.backend.entities.Attendance;
import com.employee360.backend.entities.Employee;
import com.employee360.backend.entities.User;
import com.employee360.backend.repos.AttendanceRepo;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.repos.UserRepo;

@Service
@Transactional
public class AttendanceSer {

    private final AttendanceRepo attendanceRepo;
    private final EmployeeRepo employeeRepo;
    private final UserRepo userRepo;

    public AttendanceSer(AttendanceRepo attendanceRepo, EmployeeRepo employeeRepo, UserRepo userRepo) {
        this.attendanceRepo = attendanceRepo;
        this.employeeRepo = employeeRepo;
        this.userRepo = userRepo;
    }

    // CREATE
    public AttendanceResponse saveAttendance(AttendanceRequest request) {
        Attendance attendance = new Attendance();
        attendance.setEmployee(resolveEmployee(request));
        copyRequest(request, attendance);
        return AttendanceResponse.from(attendanceRepo.save(attendance));
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepo.findAll().stream().map(AttendanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMyAttendance(String username) {
        Employee employee = resolveEmployeeForUser(username);
        return attendanceRepo.findByEmployee_IdOrderByAttendanceDateDesc(employee.getId())
                .stream().map(AttendanceResponse::from).toList();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public Optional<AttendanceResponse> getAttendanceById(Long id) {
        return attendanceRepo.findById(id).map(AttendanceResponse::from);
    }

    // UPDATE
    public Optional<AttendanceResponse> updateAttendance(AttendanceRequest request, Long id) {
        return attendanceRepo.findById(id).map(existing -> {
            if ((request.getEmployeeCode() != null && !request.getEmployeeCode().isBlank())
                    || request.getEmployeeId() != null) {
                existing.setEmployee(resolveEmployee(request));
            }
            copyRequest(request, existing);
            return AttendanceResponse.from(attendanceRepo.save(existing));
        });
    }

    // DELETE
    public boolean deleteAttendance(Long id) {

        Attendance attendance =
                attendanceRepo.findById(id).orElse(null);

        if (attendance != null) {
            attendanceRepo.delete(attendance);
            return true;
        }

        return false;
    }

    private Employee resolveEmployee(AttendanceRequest request) {
        if (request.getEmployeeCode() != null && !request.getEmployeeCode().isBlank()) {
            return employeeRepo.findByEmployeeCodeIgnoreCase(request.getEmployeeCode().trim())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Employee not found with the supplied employee code"));
        }
        if (request.getEmployeeId() != null) {
            return employeeRepo.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Employee not found with the supplied employee ID"));
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Employee code is required");
    }

    private Employee resolveEmployeeForUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Your account is not linked to an employee record");
        }
        Employee employee = employeeRepo.findByEmailIgnoreCase(user.getEmail());
        if (employee == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No employee record is linked to your account");
        }
        return employee;
    }

    private void copyRequest(AttendanceRequest request, Attendance attendance) {
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setStatus(request.getStatus());
        attendance.setCheckInTime(request.getCheckInTime());
        attendance.setCheckOutTime(request.getCheckOutTime());
    }
}
