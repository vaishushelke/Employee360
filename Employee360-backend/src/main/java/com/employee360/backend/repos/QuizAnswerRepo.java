
package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.QuizAnswer;

@Repository
public interface QuizAnswerRepo
        extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findByAttemptId(Long attemptId);

    boolean existsByAttemptIdAndQuestionId(
            Long attemptId,
            Long questionId
    );
}
