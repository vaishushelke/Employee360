package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee , Long> {

	Employee findByEmail(String email);
	
	List<Employee> findByFirstName(String firstName);
	
	List<Employee> findByStatus(String status);
	
	List<Employee> findByDesignation(String designation);
	
	List<Employee> findByFirstNameContainingIgnoreCase( String firstName);
	
	List<Employee> findByLastNameContainingIgnoreCase(String lastName);
	
	List<Employee> findByDesignationContainingIgnoreCase(String designation);

	List<Employee> findByFirstNameAndDesignation(
	            String firstName,
	            String designation
	    );
	
}
