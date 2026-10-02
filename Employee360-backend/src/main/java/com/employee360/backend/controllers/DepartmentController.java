package com.employee360.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.employee360.backend.entities.Department;
import com.employee360.backend.services.DepartmentSer;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/Department")
public class DepartmentController {

	@Autowired 
	private DepartmentSer dser;
	
	
	@PostMapping("/saved")
	public ResponseEntity<?> saveDepartment(@RequestBody  @Valid Department department ){
		dser.saveDepartment(department);
		return ResponseEntity.status(HttpStatus.OK).body("create department "+department); 
	}
	
	@GetMapping("/getalld")
	public List<Department> getAllDepartment(){
		List<Department> d =  dser.getAllDepartment();
		 return d;
	}
	
	
	@GetMapping("/getById/{id}") 
	public ResponseEntity<?> getDepartmentById( @PathVariable Long id) { 
		Department dept = dser.getDepartmentById(id); 
		if (dept != null) { 
			return ResponseEntity .status(HttpStatus.CREATED) .body(dept);
		} 
			return ResponseEntity .status(HttpStatus.NOT_FOUND) .body("Department Not Found"); 
			}
	
	@PutMapping("/updateById/{id}")
	public ResponseEntity<?> updateDepartment(@RequestBody Department dept , @PathVariable Long id){
		Department updatedept = dser.updateDepartment(dept , id);
		
		if(updatedept !=  null) {
			return ResponseEntity.status(HttpStatus.OK).body(updatedept);
		}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
	}
	
	
	@DeleteMapping("deleteById/{id}")
	public ResponseEntity<?> deleteDepartment(@PathVariable Long id){
		Boolean result = dser.deleteDepartment(id);
		
		if(result  ) {
			return ResponseEntity.status(HttpStatus.OK).body(result);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department Not Found");
	}
	
	
}
