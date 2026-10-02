package com.employee360.backend.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Department;

@Repository
public interface DepartmentRepo extends CrudRepository <Department , Long>{

}
