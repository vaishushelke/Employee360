package com.employee360.backend.entities;

import java.time.LocalDate;

import com.employee360.backend.enums.LeaveStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leaves")
public class Leave {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	
	private Long id;
	private String leaveType;
	private LocalDate startDate;
	private LocalDate endDate;
	private String reason;
	
	private LeaveStatus status;
	
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	public Leave() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Leave(Long id, String leaveType, LocalDate startDate, LocalDate endDate, String reason, LeaveStatus status,
			Employee employee) {
		super();
		this.id = id;
		this.leaveType = leaveType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.reason = reason;
		this.status = status;
		this.employee = employee;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LeaveStatus getStatus() {
		return status;
	}

	public void setStatus(LeaveStatus status) {
		this.status = status;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
	public String toString() {
		return "Leave [id=" + id + ", leaveType=" + leaveType + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", reason=" + reason + ", status=" + status + ", employee=" + employee + "]";
	}

	
	
	
	
}
