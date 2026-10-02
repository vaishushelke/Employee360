package com.employee360.backend.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee360.backend.entities.User;
import com.employee360.backend.security.CustomUserDetailsService;
import com.employee360.backend.security.JwtService;
import com.employee360.backend.services.UserSer;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserSer userSer;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;


    // ==========================================
    // REGISTER
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user) {

        User existingUser =
                userSer.getUserByUsername(
                        user.getUsername());

        if (existingUser != null) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()));

        User savedUser =
                userSer.saveUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User Registered Successfully");
    }


    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User user) {

        // Check username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                )
        );


        // Load user details
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getUsername()
                );


        // Generate JWT token
        String token =
                jwtService.generateToken(
                        userDetails
                );


        // Get user from database
        User loggedInUser =
                userSer.getUserByUsername(
                        user.getUsername()
                );


        // Prepare response
        Map<String, String> response =
                new HashMap<>();

        response.put(
                "token",
                token
        );

        response.put(
                "username",
                loggedInUser.getUsername()
        );

        response.put(
                "role",
                loggedInUser.getRole()
        );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}