package com.project.interviewprep.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.interviewprep.model.Answer;
import com.project.interviewprep.model.User;

public interface AnswerRepository extends JpaRepository<Answer, Integer>{

    Page<Answer> findByUserIdAndQuestionIdCategory(User user, String category, Pageable pageable);

    Page<Answer> findByUserId(User user, Pageable pageable);

}
