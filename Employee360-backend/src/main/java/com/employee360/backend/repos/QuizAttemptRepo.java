
package com.employee360.backend.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.QuizAttempt;

@Repository
public interface QuizAttemptRepo
        extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByEmployeeUsername(String employeeUsername);

    Optional<QuizAttempt> findByIdAndEmployeeUsername(
            Long id,
            String employeeUsername
    );

    List<QuizAttempt> findByQuizIdAndEmployeeUsername(
            Long quizId,
            String employeeUsername
    );
}
