package com.employee360.backend.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.backend.entities.Payroll;

public interface PayrollRepo extends JpaRepository<Payroll, Long> {

}
