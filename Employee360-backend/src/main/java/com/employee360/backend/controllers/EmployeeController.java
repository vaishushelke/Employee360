package com.employee360.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee360.backend.entities.Employee;
import com.employee360.backend.services.EmployeeSer;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/Employee")
public class EmployeeController {

	@Autowired 
	private EmployeeSer eser;
	
	
	@PostMapping("/saveE/{did}")
	public ResponseEntity<?> saveEmployee (@RequestBody @Valid Employee emp , @PathVariable Long did) {
		 eser.saveDataEmployee(emp , did);
		return ResponseEntity.status(HttpStatus.CREATED).body("Employee Created  "+emp);
	}
	
	@GetMapping("/getAll")
	public List<Employee>  getAllEmployee(){
		List<Employee> e = eser.getAllEmployee();
		return e;
	}
	
	@GetMapping("/getById/{id}")
	public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
		Employee emp =  eser.getEmployeeById(id);
				if(emp != null) {
					return ResponseEntity.status(HttpStatus.OK).body(emp);
				}else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found with ID: " + id);
				}
	}
	
	@PutMapping("update/{id}")
	public ResponseEntity<?> updateEmployee(@RequestBody Employee emp , @PathVariable Long id) {
		Employee uemp = eser.updateEmployee(emp,id);
		
		if(uemp != null) {
			return ResponseEntity .status(HttpStatus.OK) .body("Employee Updated Successfully: " + uemp);
		}else {
			return ResponseEntity .status(HttpStatus.NOT_FOUND) .body("Employee Not Found Id : " + id);
		}
	}
	
	@DeleteMapping("delete/{id}")
	public ResponseEntity<?> deleteEmployee(@PathVariable Long id){
		Boolean demp = eser.deleteEmployee(id);
		
		if(demp) {
			
			return ResponseEntity.status(HttpStatus.OK).body("Employee Deleted Successfully ");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Employee not found with ID: " + id);
	}
	
	@GetMapping("/getEmailBy/{email}")
	public ResponseEntity<?> getEmployeeByEmail(@PathVariable String email){
		 Employee employee = eser.findByEmail(email);

		    if (employee != null) {

		        return ResponseEntity
		                .status(HttpStatus.OK)
		                .body(employee);
		    }

		    return ResponseEntity
		            .status(HttpStatus.NOT_FOUND)
		            .body("Employee not found with email: " + email);
	}
	
	
	@GetMapping("/getFirstNameBy/{firstName}")
	public ResponseEntity<?> getEmployeeByFirstName(@PathVariable String firstName){
		
		List<Employee> employee = eser.findByFirstName(firstName);
		
		if(!employee.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(employee);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("No Employee Found with FirstName: "+firstName);
		}
	}
	
	@GetMapping("/getStatusBy/{status}")

	public ResponseEntity<?> getEmployeeByStatus(@PathVariable String status){
		
		List<Employee> employee = eser.findByStatus(status);
		
		if(!employee.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(employee);
		}else {
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body("No Employee Found With Status: "+status);
	}
	}
	
	
	
	@GetMapping("/getDesignationBy/{designation}")
	public ResponseEntity<?> getEmployeeByDesignation(@PathVariable String designation){
		
		List<Employee> employee = eser.findByDesignation(designation);
		
		if (!employee.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.OK)
	                .body(employee);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("No Employee found with designation: " + designation);
	    }
	}
	
	@GetMapping("/search/firstName/{firstName}")
	public ResponseEntity<?> SearchByFirstName(@PathVariable String firstName){
		List<Employee> employee = eser.searchByFirstName(firstName);
		
		if(!employee.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(employee);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Employee Found");
		}
	}
	
	@GetMapping("/search/lastName/{lastName}")
	public ResponseEntity<?> SearchByLastName(@PathVariable String lastName){
		List<Employee> employee= eser.searchByLastName(lastName);
		
		if(!employee.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(employee);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Employee Found");
		}
	}
	
	@GetMapping("/search/designation/{designation}")
	public ResponseEntity<?> SearchByDesignation(@PathVariable String designation){
		
		List<Employee> employee = eser.searchByDesignation(designation);
		
		if(!employee.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(employee);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Employee Found");
		}
	}
	
	@GetMapping("/search/{firstName}/{designation}")
	public ResponseEntity<?> searchByFirstNameAndDesignation(
	        @PathVariable("firstName") String firstName,
	        @PathVariable("designation") String designation) {

	    List<Employee> employee =eser.searchByFirstNameAndDesignation(firstName,designation );
	
	if(!employee.isEmpty()) {
		return ResponseEntity.status(HttpStatus.OK).body(employee);
	}else {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Empolyee Found");
	}
	}
}
