package com.employee360.backend.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.employee360.backend.entities.Employee;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.repos.LeaveRepo;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private LeaveRepo leaveRepo;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void deleteEmployeeWithLeaveRecordsReturnsConflictAndKeepsEmployee() {
        long employeeId = 42L;
        when(employeeRepo.findById(employeeId))
                .thenReturn(Optional.of(new Employee()));
        when(leaveRepo.existsByEmployee_Id(employeeId)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> employeeService.deleteEmployee(employeeId));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(employeeRepo, never()).delete(any(Employee.class));
    }

    @Test
    void deleteEmployeeWithoutLeaveRecordsDeletesEmployee() {
        long employeeId = 42L;
        Employee employee = new Employee();
        when(employeeRepo.findById(employeeId))
                .thenReturn(Optional.of(employee));
        when(leaveRepo.existsByEmployee_Id(employeeId)).thenReturn(false);

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepo).delete(employee);
    }
}
