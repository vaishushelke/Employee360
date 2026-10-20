
package com.employee360.backend.controllers;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee360.backend.dto.request.EmployeeRequest;
import com.employee360.backend.dto.response.EmployeeResponse;
import com.employee360.backend.dto.response.EmployeeProfileResponse;
import com.employee360.backend.services.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2/employees")
public class EmployeeV2Controller {

    private final EmployeeService employeeService;

    public EmployeeV2Controller(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // CREATE EMPLOYEE
    @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse response =
                employeeService.createEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL EMPLOYEES
    @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR', 'MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {

        return ResponseEntity.ok(
                employeeService.getAllEmployees());
    }

    // GET EMPLOYEE BY ID
    @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR', 'MANAGER')")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                employeeService.getEmployeeById(id));
    }

        @GetMapping("/me")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<EmployeeProfileResponse> getCurrentEmployee(Authentication authentication) {
                return ResponseEntity.ok(employeeService.getCurrentEmployee(authentication.getName()));
        }

    // UPDATE EMPLOYEE
    @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        return ResponseEntity.ok(
                employeeService.updateEmployee(id, request));
    }

    // DELETE EMPLOYEE
    @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/page")
        @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR', 'MANAGER')")
    public ResponseEntity<Page<EmployeeResponse>> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return ResponseEntity.ok(
                employeeService.getEmployees(page, size, sortBy, direction));
    }
    
    @GetMapping("/search")
        @PreAuthorize("hasAnyRole('ADMIN', 'CEO', 'HR', 'MANAGER')")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String designation) {

        return ResponseEntity.ok(
                employeeService.searchEmployees(
                        keyword, status, designation));
    }
}
