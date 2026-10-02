package com.employee360.backend.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Leave;
@Repository
public interface LeaveRepo extends JpaRepository<Leave, Long> {

}
