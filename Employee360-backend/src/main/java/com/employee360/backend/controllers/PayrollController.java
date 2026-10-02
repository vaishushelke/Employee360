package com.employee360.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.employee360.backend.entities.Payroll;
import com.employee360.backend.services.PayrollSer;


@RestController
@RequestMapping("/Payroll")
@CrossOrigin(origins = "http://localhost:5173")
public class PayrollController {

    @Autowired
    private PayrollSer payrollSer;

    // Save Payroll
    @PostMapping("/save")
    public ResponseEntity<?> savePayroll(
            @RequestBody Payroll payroll) {

        Payroll savedPayroll =
                payrollSer.savePayroll(payroll);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedPayroll);
    }

    // Get All Payroll
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllPayroll() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(payrollSer.getAllPayroll());
    }

    // Get Payroll By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPayrollById(
            @PathVariable Long id) {

        Payroll payroll =
                payrollSer.getPayrollById(id);

        if (payroll != null) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(payroll);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Payroll Not Found");
    }

    // Update Payroll
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePayroll(
            @RequestBody Payroll payroll,
            @PathVariable Long id) {

        Payroll updatedPayroll =
                payrollSer.updatePayroll(payroll, id);

        if (updatedPayroll != null) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(updatedPayroll);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Payroll Not Found");
    }

    // Delete Payroll
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePayroll(
            @PathVariable Long id) {

        boolean deleted =
                payrollSer.deletePayroll(id);

        if (deleted) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Payroll Deleted Successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Payroll Not Found");
    }
}
