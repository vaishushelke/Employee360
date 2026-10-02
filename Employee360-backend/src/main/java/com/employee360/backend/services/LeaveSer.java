package com.employee360.backend.services;

import java.util.List;

import com.employee360.backend.entities.Leave;

public interface LeaveSer {

	void applyLeave(Leave leave , Long EmployeeId);
	
	List<Leave> getAllLeaves();
	
	Leave getLeaveById(Long id);

    Leave updateLeave(Leave leave, Long id);

    Boolean deleteLeave(Long id);
		
    Leave approveLeave(Long id);

    Leave rejectLeave(Long id);
}
