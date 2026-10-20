
package com.employee360.backend.repos;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Leave;
import com.employee360.backend.enums.LeaveStatus;

@Repository
public interface LeaveRepo extends JpaRepository<Leave, Long> {

    long countByStatus(LeaveStatus status);

    boolean existsByEmployee_Id(Long employeeId);

    List<Leave> findByEmployee_IdOrderByStartDateDesc(Long employeeId);

    boolean existsByEmployee_IdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId,
            Collection<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate);
}
