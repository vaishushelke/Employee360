package com.employee360.backend.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.backend.entities.User;

public interface UserRepo extends JpaRepository<User, Long>{

	Optional<User> findByUsername(String username);
	
}
