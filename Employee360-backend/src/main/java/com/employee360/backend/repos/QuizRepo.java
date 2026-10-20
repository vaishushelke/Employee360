
package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Quiz;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, Long> {

    List<Quiz> findByActiveTrueAndArchivedFalse();

    List<Quiz> findAllByOrderByCreatedAtDesc();

}
