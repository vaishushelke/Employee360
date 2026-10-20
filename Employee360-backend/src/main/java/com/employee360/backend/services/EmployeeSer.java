
package com.employee360.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.Department;
import com.employee360.backend.entities.Employee;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.EmployeeRepo;

@Service
public class EmployeeSer {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    // CREATE
    public void saveDataEmployee(Employee emp, Long did) {

        Department department =
                departmentRepo.findById(did)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Department not found with ID: " + did
                        )
                    );

        emp.setDepartment(department);
        employeeRepo.save(emp);
    }

    // READ ALL
    public List<Employee> getAllEmployee() {
        return employeeRepo.findAll();
    }

    // READ BY ID
    public Employee getEmployeeById(Long id) {
        return employeeRepo.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Employee not found with ID: " + id
                    )
                );
    }

    // UPDATE
    public Employee updateEmployee(Employee emp, Long id) {

        Employee existing =
                employeeRepo.findById(id)
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Employee not found with ID: " + id
                        )
                    );

        existing.setEmployeeCode(emp.getEmployeeCode());
        existing.setFirstName(emp.getFirstName());
        existing.setLastName(emp.getLastName());
        existing.setEmail(emp.getEmail());
        existing.setJoiningDate(emp.getJoiningDate());
        existing.setDesignation(emp.getDesignation());
        existing.setSalary(emp.getSalary());
        existing.setStatus(emp.getStatus());
        existing.setDepartment(emp.getDepartment());

        return employeeRepo.save(existing);
    }

    // DELETE
    public Boolean deleteEmployee(Long id) {

        if (!employeeRepo.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Employee not found with ID: " + id);
        }

        employeeRepo.deleteById(id);
        return true;
    }

    // SEARCH BY EMAIL
    public Employee findByEmail(String email) {
        return employeeRepo.findByEmail(email);
    }

    // SEARCH BY FIRST NAME
    public List<Employee> findByFirstName(String firstName) {
        return employeeRepo.findByFirstName(firstName);
    }

    // SEARCH BY STATUS
    public List<Employee> findByStatus(String status) {
        return employeeRepo.findByStatus(status);
    }

    // SEARCH BY DESIGNATION
    public List<Employee> findByDesignation(String designation) {
        return employeeRepo.findByDesignation(designation);
    }

    // SEARCH BY FIRST NAME CONTAINING
    public List<Employee> searchByFirstName(String firstName) {
        return employeeRepo
                .findByFirstNameContainingIgnoreCase(firstName);
    }

    // SEARCH BY LAST NAME CONTAINING
    public List<Employee> searchByLastName(String lastName) {
        return employeeRepo
                .findByLastNameContainingIgnoreCase(lastName);
    }

    // SEARCH BY DESIGNATION CONTAINING
    public List<Employee> searchByDesignation(String designation) {
        return employeeRepo
                .findByDesignationContainingIgnoreCase(designation);
    }

    // SEARCH BY FIRST NAME AND DESIGNATION
    public List<Employee> searchByFirstNameAndDesignation(
            String firstName, String designation) {

        return employeeRepo.findByFirstNameAndDesignation(
                firstName, designation);
    }
    
    
}
