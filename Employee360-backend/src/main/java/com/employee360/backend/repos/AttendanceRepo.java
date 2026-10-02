package com.employee360.backend.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.backend.entities.Attendance;

public interface AttendanceRepo extends JpaRepository<Attendance , Long>{

}
