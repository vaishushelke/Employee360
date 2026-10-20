
package com.employee360.backend.repos;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Attendance;
import com.employee360.backend.enums.AttendanceStatus;
import java.util.List;

@Repository
public interface AttendanceRepo
        extends JpaRepository<Attendance, Long> {

    long countByAttendanceDate(LocalDate attendanceDate);

    long countByAttendanceDateAndStatus(
            LocalDate attendanceDate,
            AttendanceStatus status);

        List<Attendance> findByEmployee_IdOrderByAttendanceDateDesc(Long employeeId);
}
