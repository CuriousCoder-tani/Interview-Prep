package com.project.interviewprep.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.interviewprep.model.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
   

}
