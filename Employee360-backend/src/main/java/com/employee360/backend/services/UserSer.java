package com.employee360.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.User;
import com.employee360.backend.repos.UserRepo;

@Service
public class UserSer {
	

		@Autowired
		private UserRepo userRepo;
		
		public User saveUser(User user) {
			return userRepo.save(user);
		}
		
		public User getUserByUsername(String username) {
			return userRepo.findByUsername(username).orElse(null);
		}
		
		
		
	}



