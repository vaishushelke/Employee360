package com.employee360.backend.services;

import java.util.List;

import com.employee360.backend.entities.Department;

public interface DepartmentSer {

	void saveDepartment(Department department);

	List<Department> getAllDepartment();

	Department getDepartmentById(Long id);

	Department updateDepartment(Department dept, Long id);

	Boolean deleteDepartment(Long id);


}
