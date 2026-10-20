
package com.employee360.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.employee360.backend.dto.request.AttendanceRequest;
import com.employee360.backend.dto.response.AttendanceResponse;
import com.employee360.backend.services.AttendanceSer;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/Attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    @Autowired
    private AttendanceSer attendanceSer;

    // CREATE
    @PostMapping("/save")
        public ResponseEntity<AttendanceResponse> saveAttendance(
                        @Valid @RequestBody AttendanceRequest attendance) {

                AttendanceResponse savedAttendance = attendanceSer.saveAttendance(attendance);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedAttendance);
    }

    // GET ALL
    @GetMapping("/getAll")
        public ResponseEntity<java.util.List<AttendanceResponse>> getAllAttendance() {
                return ResponseEntity.ok(attendanceSer.getAllAttendance());
    }

        @GetMapping("/my")
        public ResponseEntity<java.util.List<AttendanceResponse>> getMyAttendance(Authentication authentication) {
                return ResponseEntity.ok(attendanceSer.getMyAttendance(authentication.getName()));
        }

    // GET BY ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAttendanceById(
            @PathVariable Long id) {

        return attendanceSer.getAttendanceById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Attendance not found"));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAttendance(
            @Valid @RequestBody AttendanceRequest attendance,
            @PathVariable Long id) {

        return attendanceSer.updateAttendance(attendance, id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Attendance not found"));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAttendance(
            @PathVariable Long id) {

        boolean deleted =
                attendanceSer.deleteAttendance(id);

        if (deleted) {
            return ResponseEntity.ok(
                    "Attendance Deleted Successfully");
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Attendance Not Found");
    }
}
