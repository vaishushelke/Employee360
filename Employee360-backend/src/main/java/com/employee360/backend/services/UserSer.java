
package com.employee360.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee360.backend.entities.User;
import com.employee360.backend.repos.UserRepo;

@Service
public class UserSer {

    @Autowired
    private UserRepo userRepo;

    // SAVE USER
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    // FIND USER BY USERNAME
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    // FIND USER BY EMAIL
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    // CHECK WHETHER EMAIL EXISTS
    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }
}
