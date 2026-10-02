package com.employee360.backend.daos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.Department;
import com.employee360.backend.entities.Employee;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.services.EmployeeSer;

@Service
public class EmployeeDao implements EmployeeSer {
	
	@Autowired
	private EmployeeRepo erepo;
	@Autowired 
	private DepartmentRepo drepo;
	
	@Override
	public void saveDataEmployee(Employee emp , Long did) {
		// TODO Auto-generated method stub
		Department d = drepo.findById(did).get();
		emp.setDepartment(d);
		erepo.save(emp);
		
	}

	@Override
	public List<Employee> getAllEmployee() {
		// TODO Auto-generated method stub
		return (List<Employee>) erepo.findAll();
	}

	@Override
	public Employee getEmployeeById(Long id) {
		// TODO Auto-generated method stub
//		return erepo.findById(id).orElse(null);
		
		return erepo.findById(id)
	            .orElseThrow(() ->
	                new ResourceNotFoundException(
	                    "Employee not found with ID: " + id
	                )
	            );	
	}

	@Override
	public Employee updateEmployee(Employee emp, Long id) {
		// TODO Auto-generated method stub
		Employee oldemp = erepo.findById(id).orElseThrow(()->
		new ResourceNotFoundException(
				"Employee not found with ID: "+id )
		);
		
		if(oldemp != null ) {
			oldemp.setEmployeeCode(emp.getEmployeeCode());
			oldemp.setFirstName(emp.getFirstName());
			oldemp.setLastName(emp.getLastName());
			oldemp.setEmail(emp.getEmail());
			oldemp.setJoiningDate(emp.getJoiningDate());
			oldemp.setDesignation(emp.getDesignation());
			oldemp.setSalary(emp.getSalary());
			oldemp.setStatus(emp.getStatus());
			oldemp.setDepartment(emp.getDepartment());
			
			return erepo.save(oldemp);
		}
		return null;
	}

	@Override
	public Boolean deleteEmployee(Long id) {
		// TODO Auto-generated method stub
		
		if(erepo.existsById(id)) {
			
			   throw new ResourceNotFoundException(
		                "Employee not found with ID: " + id
		        );
		}
		  erepo.deleteById(id);

		    return true;
	}

	@Override
	public Employee findByEmail(String email) {
		// TODO Auto-generated method stub
		return erepo.findByEmail(email);
	}

	@Override
	public List<Employee> findByFirstName(String firstName) {
		// TODO Auto-generated method stub
		return erepo.findByFirstName(firstName);
	}

	@Override
	public List<Employee> findByStatus(String status) {
		// TODO Auto-generated method stub
		return erepo.findByStatus(status);
	}

	@Override
	public List<Employee> findByDesignation(String designation) {
		// TODO Auto-generated method stub
		return erepo.findByDesignation(designation);
	}

	@Override
	public List<Employee> searchByFirstName(String firstName) {
		// TODO Auto-generated method stub
		return erepo.findByFirstNameContainingIgnoreCase(firstName);
	}

	@Override
	public List<Employee> searchByLastName(String lastName) {
		// TODO Auto-generated method stub
		return erepo.findByLastNameContainingIgnoreCase(lastName);
	}

	@Override
	public List<Employee> searchByDesignation(String designation) {
		// TODO Auto-generated method stub
		return erepo.findByDesignationContainingIgnoreCase(designation);
	}

	@Override
	public List<Employee> searchByFirstNameAndDesignation(String firstName, String designation) {
		// TODO Auto-generated method stub
		return erepo.findByFirstNameAndDesignation(firstName, designation);
	}
	


}
