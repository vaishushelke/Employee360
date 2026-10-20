
package com.employee360.backend.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee360.backend.entities.EmailOtp;
import com.employee360.backend.repos.EmailOtpRepo;

@Service
public class EmailOtpService {

    private final EmailOtpRepo emailOtpRepo;
    private final EmailService emailService;

    public EmailOtpService(
            EmailOtpRepo emailOtpRepo,
            EmailService emailService) {
        this.emailOtpRepo = emailOtpRepo;
        this.emailService = emailService;
    }

    
    @Transactional
    public void sendOtp(String email) {

        // Generate a six-digit OTP
        String otp = String.format(
                "%06d",
                new SecureRandom().nextInt(1_000_000));

        // Create or update the OTP record
        EmailOtp emailOtp = emailOtpRepo
                .findByEmail(email)
                .orElseGet(EmailOtp::new);

        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiryTime(
                LocalDateTime.now().plusMinutes(5));
        emailOtp.setVerified(false);

        emailOtpRepo.save(emailOtp);

        // Send OTP to the user's email
        emailService.sendEmail(
                email,
                "Employee360 Email Verification",
                "Your OTP is: " + otp
                + "\n\nThis OTP expires in 5 minutes.");
    }
    
    
    @Transactional
    public boolean verifyOtp(String email, String enteredOtp) {

        EmailOtp emailOtp = emailOtpRepo
                .findByEmail(email)
                .orElse(null);

        if (emailOtp == null) {
            return false;
        }

        // Check whether OTP has expired
        if (emailOtp.getExpiryTime() == null
                || LocalDateTime.now().isAfter(
                        emailOtp.getExpiryTime())) {
            return false;
        }

        // Check whether OTP matches
        if (!emailOtp.getOtp().equals(enteredOtp)) {
            return false;
        }

        // Mark OTP as verified
        emailOtp.setVerified(true);
        emailOtpRepo.save(emailOtp);

        return true;
    }
   
}
