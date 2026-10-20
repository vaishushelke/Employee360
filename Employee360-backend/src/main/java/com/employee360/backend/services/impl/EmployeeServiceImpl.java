
package com.employee360.backend.services.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.backend.dto.request.EmployeeRequest;
import com.employee360.backend.dto.response.EmployeeProfileResponse;
import com.employee360.backend.dto.response.EmployeeResponse;
import com.employee360.backend.entities.Department;
import com.employee360.backend.entities.Employee;
import com.employee360.backend.entities.User;
import com.employee360.backend.exception.DuplicateResourceException;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.repos.LeaveRepo;
import com.employee360.backend.repos.UserRepo;
import com.employee360.backend.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DepartmentRepo departmentRepo;
    private final LeaveRepo leaveRepo;
        private final UserRepo userRepo;

    public EmployeeServiceImpl(
            EmployeeRepo employeeRepo,
            DepartmentRepo departmentRepo,
            LeaveRepo leaveRepo,
            UserRepo userRepo) {

        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
        this.leaveRepo = leaveRepo;
        this.userRepo = userRepo;
    }

    // CREATE
    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        Department department = departmentRepo
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with ID: "
                                        + request.getDepartmentId()));

        checkDuplicateEmployee(
                request.getEmployeeCode(),
                request.getEmail(),
                null);

        Employee employee = new Employee();

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
        employee.setDepartment(department);

        Employee savedEmployee = employeeRepo.save(employee);

        return toResponse(savedEmployee);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with ID: " + id));

        return toResponse(employee);
    }

        @Override
        @Transactional(readOnly = true)
        public EmployeeProfileResponse getCurrentEmployee(String username) {
                User user = userRepo.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                if (user.getEmail() == null || user.getEmail().isBlank()) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT, "Your account is not linked to an employee record");
                }
                Employee employee = employeeRepo.findByEmailIgnoreCase(user.getEmail());
                if (employee == null) {
                        throw new ResourceNotFoundException("No employee record is linked to your account");
                }
                return EmployeeProfileResponse.from(employee);
        }

    // UPDATE
    @Override
    public EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request) {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with ID: " + id));

        Department department = departmentRepo
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with ID: "
                                        + request.getDepartmentId()));

        checkDuplicateEmployee(
                request.getEmployeeCode(),
                request.getEmail(),
                id);

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDesignation(request.getDesignation());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
        employee.setDepartment(department);

        Employee updatedEmployee = employeeRepo.save(employee);

        return toResponse(updatedEmployee);
    }

    // DELETE
    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + id));

        if (leaveRepo.existsByEmployee_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This employee has leave records and cannot be deleted. Update the employee's status to inactive to retain their history.");
        }

        employeeRepo.delete(employee);
    }

    // CHECK DUPLICATE EMPLOYEE CODE AND EMAIL
    private void checkDuplicateEmployee(
            String employeeCode,
            String email,
            Long currentEmployeeId) {

        employeeRepo.findAll().stream()
                .filter(employee ->
                        currentEmployeeId == null
                                || !employee.getId()
                                        .equals(currentEmployeeId))
                .filter(employee ->
                        employeeCode != null
                                && employee.getEmployeeCode() != null
                                && employee.getEmployeeCode()
                                        .equalsIgnoreCase(employeeCode)
                        || email != null
                                && employee.getEmail() != null
                                && employee.getEmail()
                                        .equalsIgnoreCase(email))
                .findFirst()
                .ifPresent(employee -> {

                    if (employeeCode != null
                            && employee.getEmployeeCode() != null
                            && employee.getEmployeeCode()
                                    .equalsIgnoreCase(employeeCode)) {

                        throw new DuplicateResourceException(
                                "Employee code already exists");
                    }

                    throw new DuplicateResourceException(
                            "Email already exists");
                });
    }

    // SEARCH EMPLOYEES
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployees(
            String keyword,
            String status,
            String designation) {

        String searchKeyword = keyword == null
                ? ""
                : keyword.trim().toLowerCase(Locale.ROOT);

        return employeeRepo.findAll().stream()
                .filter(e ->
                        searchKeyword.isEmpty()
                        || (e.getFirstName() != null
                                && e.getFirstName().toLowerCase(Locale.ROOT)
                                        .contains(searchKeyword))
                        || (e.getLastName() != null
                                && e.getLastName().toLowerCase(Locale.ROOT)
                                        .contains(searchKeyword))
                        || (e.getEmail() != null
                                && e.getEmail().toLowerCase(Locale.ROOT)
                                        .contains(searchKeyword)))
                .filter(e ->
                        status == null || status.isBlank()
                        || (e.getStatus() != null
                                && e.getStatus().equalsIgnoreCase(status)))
                .filter(e ->
                        designation == null || designation.isBlank()
                        || (e.getDesignation() != null
                                && e.getDesignation()
                                        .equalsIgnoreCase(designation)))
                .map(this::toResponse)
                .toList();
    }

    // PAGINATION AND SORTING
    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployees(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0");
        }

        List<String> allowedSortFields = List.of(
                "id",
                "employeeCode",
                "firstName",
                "lastName",
                "email",
                "designation",
                "joiningDate",
                "salary",
                "status");

        if (sortBy == null || !allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy);
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return employeeRepo.findAll(pageable)
                .map(this::toResponse);
    }

    
    private EmployeeResponse toResponse(Employee employee) {

        EmployeeResponse response = new EmployeeResponse();

        response.setId(employee.getId());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setDesignation(employee.getDesignation());
        response.setJoiningDate(employee.getJoiningDate());
        response.setSalary(employee.getSalary());
        response.setStatus(employee.getStatus());

        if (employee.getDepartment() != null) {
            response.setDepartmentId(
                    employee.getDepartment().getId());

            response.setDepartmentName(
                    employee.getDepartment().getName());
        }

        return response;
    }
   
}
