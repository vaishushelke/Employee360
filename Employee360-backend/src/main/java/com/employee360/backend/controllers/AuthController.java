
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
import com.employee360.backend.enums.Role;
import com.employee360.backend.security.CustomUserDetailsService;
import com.employee360.backend.security.JwtService;
import com.employee360.backend.services.EmailOtpService;
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

    @Autowired
    private EmailOtpService emailOtpService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getUsername() == null
                || user.getUsername().isBlank()
                || user.getPassword() == null
                || user.getPassword().isBlank()
                || user.getEmail() == null
                || user.getEmail().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Username, password, and email are required");
        }

        if (userSer.getUserByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }

        if (userSer.emailExists(user.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        // Use the role from the request body; default to EMPLOYEE if none provided.
        if (user.getRole() == null) {
            user.setRole(Role.EMPLOYEE);
        }
        user.setEmailVerified(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userSer.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Registration successful. Please request an OTP to verify your email.");
    }

    // SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Email is required");
        }

        User user = userSer.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No account found with this email");
        }

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest()
                    .body("Email is already verified");
        }

        try {
            emailOtpService.sendOtp(email);

            return ResponseEntity.ok(
                    "OTP sent successfully to your email");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to send OTP. Check the email configuration.");
        }
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || email.isBlank()
                || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Email and OTP are required");
        }

        User user = userSer.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No account found with this email");
        }

        if (user.isEmailVerified()) {
            return ResponseEntity.ok("Email is already verified");
        }

        boolean verified = emailOtpService.verifyOtp(email, otp);

        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP");
        }

        user.setEmailVerified(true);
        userSer.saveUser(user);

        return ResponseEntity.ok(
                "Email verified successfully. You can now log in.");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        User loggedInUser = userSer.getUserByUsername(user.getUsername());

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        if (!loggedInUser.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please verify your email before logging in");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        String token = jwtService.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", loggedInUser.getUsername());
        response.put("role", loggedInUser.getRole().name());

        return ResponseEntity.ok(response);
    }
}
