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

import com.employee360.backend.entities.Leave;
import com.employee360.backend.services.LeaveSer;

@RestController
@RequestMapping("/Leave")
public class LeaveController {
	
	@Autowired
	private LeaveSer lser;
	
	@PostMapping("/apply/{employeeId}")
	public ResponseEntity<?> applyLeave(
			@RequestBody Leave leave,
			@PathVariable Long employeeId){
		lser.applyLeave(leave, employeeId);
		
		return ResponseEntity.status(HttpStatus.OK).body("Leave Applied Successfully !");
	}
	
	@GetMapping("/getAll")
	public ResponseEntity<?> getAllLeaves(){
	
	List<Leave> leaves = lser.getAllLeaves();
	
	return ResponseEntity.status(HttpStatus.OK).body(leaves);
	}
	
	
	@GetMapping("/getById/{id}")
	public ResponseEntity<?> getLeaveById(
			@PathVariable Long id){
		
	Leave leave = lser.getLeaveById(id);	
	
		if(leave != null) {
			return ResponseEntity.status(HttpStatus.OK).body(leave);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave Not Found");
	}
	
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateLeave(
			@RequestBody Leave leave,
			@PathVariable Long id){
		
		Leave updatedLeave = lser.updateLeave(leave, id);
		
		if(updatedLeave != null) {
			return ResponseEntity.status(HttpStatus.OK).body(updatedLeave);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave Not Found");
		
	}
	
	
	@PutMapping("/approve/{id}")
	public ResponseEntity<?> approveLeave(@PathVariable Long id){
		
		Leave leave = lser.approveLeave(id);
		
		if(leave != null) {
			return ResponseEntity.status(HttpStatus.OK).body("Leave Approved Successfully");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave Not Found");
	}
	
	
	@PutMapping("/reject/{id}")
	public ResponseEntity<?> rejectLeave(@PathVariable Long id){
		
		Leave leave = lser.rejectLeave(id);
		
		if(leave != null) {
			 return ResponseEntity.status(HttpStatus.OK).body("Leave Rejected Successfully");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave Not Found");
	}
	
	
	 @DeleteMapping("/delete/{id}")
	    public ResponseEntity<?> deleteLeave(
	            @PathVariable Long id) {

	        Boolean result =
	                lser.deleteLeave(id);

	        if (result) {

	            return ResponseEntity
	                    .status(HttpStatus.OK)
	                    .body("Leave Deleted Successfully");
	        }

	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body("Leave Not Found");
	    }
	
}
