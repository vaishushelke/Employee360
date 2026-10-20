
package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.employee360.backend.entities.Employee;
import jakarta.persistence.LockModeType;

@Repository
public interface EmployeeRepo
        extends JpaRepository<Employee, Long> {

    Employee findByEmail(String email);

    Employee findByEmailIgnoreCase(String email);

        java.util.Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select employee from Employee employee where employee.id = :employeeId")
    java.util.Optional<Employee> lockById(@Param("employeeId") Long employeeId);
    
    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);
    
    Page<Employee> findAll(Pageable pageable);

    boolean existsByEmail(String email);

    List<Employee> findByFirstName(String firstName);

    List<Employee> findByStatus(String status);

    List<Employee> findByDesignation(String designation);

    List<Employee> findByFirstNameContainingIgnoreCase(
            String firstName);

    List<Employee> findByLastNameContainingIgnoreCase(
            String lastName);

    List<Employee> findByDesignationContainingIgnoreCase(
            String designation);

    List<Employee> findByFirstNameAndDesignation(
            String firstName,
            String designation);
}