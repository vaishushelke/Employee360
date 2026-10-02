package com.employee360.backend.entities;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "employees")

public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	private Long id ;
	
	@NotBlank(message = "Employee Code Not be Empty")
	private String employeeCode;
	
	@NotBlank(message = "First name cannot be empty")
	private String firstName;
	
	@NotBlank(message = "Last name cannot be empty")
	private String lastName ;
	
	@NotBlank(message = "Email cannot be empty")
	@Email(message = "Please enter a valid email")
	private String email;
	
	@NotBlank(message = "Designation cannot be empty")
	private String designation;
	
	@PastOrPresent
	@NotNull(message = "Joining date is required")
	private LocalDate joiningDate;
	
	@NotNull
	@Positive(message = "Salary must be greater than zero")
	private double salary;
	
	@NotBlank(message = "Status cannot be empty")
	private String status;
	
	
	@ManyToOne
	@JoinColumn(name = "department_id")
	
	private Department department;
	
	@OneToMany(mappedBy = "employee")
	@JsonIgnore
	private List<Leave> leaves;


	public Employee() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Employee(Long id, String employeeCode, String firstName, String lastName, String email, String designation,
			LocalDate joiningDate, double salary, String status, Department department) {
		super();
		this.id = id;
		this.employeeCode = employeeCode;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.designation = designation;
		this.joiningDate = joiningDate;
		this.salary = salary;
		this.status = status;
		this.department = department;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getEmployeeCode() {
		return employeeCode;
	}


	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getDesignation() {
		return designation;
	}


	public void setDesignation(String designation) {
		this.designation = designation;
	}


	public LocalDate getJoiningDate() {
		return joiningDate;
	}


	public void setJoiningDate(LocalDate joiningDate) {
		this.joiningDate = joiningDate;
	}


	public double getSalary() {
		return salary;
	}


	public void setSalary(double salary) {
		this.salary = salary;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Department getDepartment() {
		return department;
	}


	public void setDepartment(Department department) {
		this.department = department;
	}


	@Override
	public String toString() {
		return "Employee [id=" + id + ", employeeCode=" + employeeCode + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", designation=" + designation + ", joiningDate=" + joiningDate
				+ ", salary=" + salary + ", status=" + status + ", department=" + department + "]";
	}

	
	
	


}