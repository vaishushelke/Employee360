
package com.employee360.backend.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.employee360.backend.dto.request.EmployeeRequest;
import com.employee360.backend.dto.response.EmployeeProfileResponse;
import com.employee360.backend.dto.response.EmployeeResponse;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Long id);

        EmployeeProfileResponse getCurrentEmployee(String username);

    EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request);

    void deleteEmployee(Long id);
    
    Page<EmployeeResponse> getEmployees(
            int page, int size, String sortBy, String direction);
    
    List<EmployeeResponse> searchEmployees(
            String keyword, String status, String designation);
}
