package com.employee360.backend.daos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.Employee;
import com.employee360.backend.entities.Leave;
import com.employee360.backend.enums.LeaveStatus;
import com.employee360.backend.repos.EmployeeRepo;
import com.employee360.backend.repos.LeaveRepo;
import com.employee360.backend.services.LeaveSer;

@Service
public class LeaveDao implements LeaveSer {

	@Autowired
	private LeaveRepo lrepo;
	
	@Autowired
	private EmployeeRepo erepo;
	
	@Override
	public void applyLeave(Leave leave, Long EmployeeId) {
		// TODO Auto-generated method stub
		Employee employee = erepo.findById(EmployeeId).orElse(null);
		if(employee != null) {
			 leave.setEmployee(employee);

	            lrepo.save(leave);
		}
	}

	@Override
	public List<Leave> getAllLeaves() {
		// TODO Auto-generated method stub
		return lrepo.findAll();
	}

	@Override
	public Leave getLeaveById(Long id) {
		// TODO Auto-generated method stub
		return lrepo.findById(id).orElse(null);
	}

	@Override
	public Leave updateLeave(Leave leave, Long id) {
		// TODO Auto-generated method stub
		Leave oldLeave = lrepo.findById(id).orElse(null);

        if (oldLeave != null) {

            oldLeave.setLeaveType(leave.getLeaveType());
            oldLeave.setStartDate(leave.getStartDate());
            oldLeave.setEndDate(leave.getEndDate());
            oldLeave.setReason(leave.getReason());
            oldLeave.setStatus(leave.getStatus());

            return lrepo.save(oldLeave);
        }

        return null;
	}

	@Override
	public Boolean deleteLeave(Long id) {
		// TODO Auto-generated method stub
		if (lrepo.existsById(id)) {

            lrepo.deleteById(id);

            return true;
        }

        return false;
	}

	@Override
	public Leave approveLeave(Long id) {
		// TODO Auto-generated method stub

	    Leave leave = lrepo.findById(id).orElse(null);

	    if (leave != null) {

	        leave.setStatus(LeaveStatus.APPROVED);

	        return lrepo.save(leave);
	    }

	    return null;
	}

	@Override
	public Leave rejectLeave(Long id) {
		// TODO Auto-generated method stub
		 Leave leave = lrepo.findById(id).orElse(null);

		    if (leave != null) {

		        leave.setStatus(LeaveStatus.REJECTED);

		        return lrepo.save(leave);
		    }

		    return null;
	}

}
