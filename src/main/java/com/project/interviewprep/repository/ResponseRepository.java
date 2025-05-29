package com.project.interviewprep.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.interviewprep.model.Answer;
import com.project.interviewprep.model.Response;

public interface ResponseRepository extends JpaRepository<Response, Integer>{

    Optional<Response> findByAnswer(Answer answer);

}
