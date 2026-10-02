package com.employee360.backend.daos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.Department;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.DepartmentRepo;
import com.employee360.backend.services.DepartmentSer;

@Service
public class DepartmentDao implements DepartmentSer {

	@Autowired
	private DepartmentRepo drepo;
	
	@Override
	public void saveDepartment(Department department) {
		// TODO Auto-generated method stub
		drepo.save(department);
	}

	@Override
	public List<Department> getAllDepartment() {
		// TODO Auto-generated method stub
		return (List<Department>) drepo.findAll();
	}

	@Override
	public Department getDepartmentById(Long id) {
		// TODO Auto-generated method stub
		
		return drepo.findById(id).orElseThrow(()-> 
		new ResourceNotFoundException(
				"Department not found with ID : "+id
				)
		);

	}

	@Override
	public Department updateDepartment(Department dept, Long id) {
		// TODO Auto-generated method stub
		Department olddept = drepo.findById(id).orElse(null);
		if (olddept != null) {
			
			olddept.setName(dept.getName());
			olddept.setDescription(dept.getDescription());
		
			return drepo.save(olddept);
		}
		return null;
		}

	@Override
	public Boolean deleteDepartment(Long id) {
		// TODO Auto-generated method stub
		
		if (drepo.existsById(id)) { 
			drepo.deleteById(id); 
				return true; 
			}
				return false; 

	}
	

	

	
}
