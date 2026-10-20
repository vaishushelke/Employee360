
package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.QuizOption;

@Repository
public interface QuizOptionRepo
        extends JpaRepository<QuizOption, Long> {

    List<QuizOption> findByQuestionId(Long questionId);

}
