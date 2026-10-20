
package com.employee360.backend.repos;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee360.backend.entities.User;
import com.employee360.backend.enums.Role;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findByRoleIn(List<Role> roles);

    boolean existsByEmail(String email);
}