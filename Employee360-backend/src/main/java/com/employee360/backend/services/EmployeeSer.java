package com.employee360.backend.services;

import java.util.List;

import com.employee360.backend.entities.Employee;

public interface EmployeeSer  {

	void saveDataEmployee(Employee emp , Long did);

	List<Employee> getAllEmployee();

	Employee getEmployeeById(Long id);

	Employee updateEmployee(Employee emp, Long id);

	Boolean deleteEmployee(Long id);
	
	Employee findByEmail (String email);
	
	List<Employee> findByFirstName(String firstName);
	
	List<Employee> findByStatus(String status);
	
	List<Employee> findByDesignation(String designation);
	
	List<Employee> searchByFirstName(String firstName);

	List<Employee> searchByLastName(String lastName);

	List<Employee> searchByDesignation(String designation);

	List<Employee> searchByFirstNameAndDesignation(
	        String firstName,
	        String designation
	);

}
