
package com.employee360.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.Department;
import com.employee360.backend.exception.ResourceNotFoundException;
import com.employee360.backend.repos.DepartmentRepo;


@Service
public class DepartmentSer {

    @Autowired
    private DepartmentRepo departmentRepo;

    // CREATE
    public void saveDepartment(Department department) {
        departmentRepo.save(department);
    }

    // READ ALL
    public List<Department> getAllDepartment() {
        return departmentRepo.findAll();
    }

    // READ BY ID
    public Department getDepartmentById(Long id) {
        return departmentRepo.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Department not found with ID: " + id
                    )
                );
    }

    // UPDATE
    public Department updateDepartment(
            Department dept, Long id) {

        Department existing =
                departmentRepo.findById(id).orElse(null);

        if (existing != null) {
            existing.setName(dept.getName());
            existing.setDescription(dept.getDescription());

            return departmentRepo.save(existing);
        }

        return null;
    }

    // DELETE
    public Boolean deleteDepartment(Long id) {
        if (departmentRepo.existsById(id)) {
            departmentRepo.deleteById(id);
            return true;
        }

        return false;
    }
}
