package com.employee360.backend.entities;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table( name = "Departments")
public class Department {

	@Id
	@GeneratedValue(strategy =  GenerationType.AUTO)
	
	private Long id ;
	
	@NotBlank(message = "Department name cannot be empty")
	private	String name ;
	
	@NotBlank(message = "Department description cannot be empty")

	private String description;
	
	@JsonIgnore
	@OneToMany(mappedBy = "department")
	private List<Employee> employees;
	
	public Department() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "Department [id=" + id + ", name=" + name + ", description=" + description + "]";
	}


	public Department(Long id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	
	
	
}
