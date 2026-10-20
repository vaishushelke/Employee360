
package com.employee360.backend.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.backend.entities.EmailOtp;

public interface EmailOtpRepo extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByEmail(String email);

    void deleteByEmail(String email);
}
