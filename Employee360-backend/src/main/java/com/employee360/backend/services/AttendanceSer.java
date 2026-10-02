package com.employee360.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.Attendance;
import com.employee360.backend.repos.AttendanceRepo;


@Service
public class AttendanceSer {

    @Autowired
    private AttendanceRepo attendanceRepo;


    // Save Attendance
    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepo.save(attendance);
    }


    // Get All Attendance
    public List<Attendance> getAllAttendance() {
        return attendanceRepo.findAll();
    }


    // Get Attendance By ID
    public Attendance getAttendanceById(Long id) {

        return attendanceRepo.findById(id).orElse(null);
    }


    // Update Attendance
    public Attendance updateAttendance(Attendance attendance, Long id) {

        Attendance existingAttendance =
                attendanceRepo.findById(id).orElse(null);

        if (existingAttendance != null) {

            existingAttendance.setEmployeeId(
                    attendance.getEmployeeId());

            existingAttendance.setAttendanceDate(
                    attendance.getAttendanceDate());

            existingAttendance.setStatus(
                    attendance.getStatus());

            existingAttendance.setCheckInTime(
                    attendance.getCheckInTime());

            existingAttendance.setCheckOutTime(
                    attendance.getCheckOutTime());

            return attendanceRepo.save(existingAttendance);
        }

        return null;
    }


    // Delete Attendance
    public boolean deleteAttendance(Long id) {

        Attendance attendance =
                attendanceRepo.findById(id).orElse(null);

        if (attendance != null) {

            attendanceRepo.delete(attendance);

            return true;
        }

        return false;
    }
}