package com.employee360.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.employee360.backend.entities.Attendance;
import com.employee360.backend.services.AttendanceSer;

@RestController
@RequestMapping("/Attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    @Autowired
    private AttendanceSer attendanceSer;


    // Save Attendance
    @PostMapping("/save")
    public ResponseEntity<?> saveAttendance(
            @RequestBody Attendance attendance) {

        Attendance savedAttendance =
                attendanceSer.saveAttendance(attendance);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAttendance);
    }


    // Get All Attendance
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAttendance() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(attendanceSer.getAllAttendance());
    }


    // Get Attendance By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAttendanceById(
            @PathVariable Long id) {

        Attendance attendance =
                attendanceSer.getAttendanceById(id);

        if (attendance != null) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(attendance);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Attendance Not Found");
    }


    // Update Attendance
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAttendance(
            @RequestBody Attendance attendance,
            @PathVariable Long id) {

        Attendance updatedAttendance =
                attendanceSer.updateAttendance(attendance, id);

        if (updatedAttendance != null) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(updatedAttendance);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Attendance Not Found");
    }


    // Delete Attendance
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAttendance(
            @PathVariable Long id) {

        boolean deleted =
                attendanceSer.deleteAttendance(id);

        if (deleted) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Attendance Deleted Successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Attendance Not Found");
    }
}