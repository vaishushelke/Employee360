package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.LeaveApprovalHistory;

@Repository
public interface LeaveApprovalHistoryRepo
        extends JpaRepository<LeaveApprovalHistory, Long> {

    List<LeaveApprovalHistory> findByLeave_IdOrderByDecidedAtDesc(Long leaveId);
}
