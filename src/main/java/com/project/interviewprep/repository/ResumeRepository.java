package com.project.interviewprep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.project.interviewprep.model.Resume;
import com.project.interviewprep.model.User;

public interface ResumeRepository extends JpaRepository<Resume, Integer>{

    Resume findByUser(User user);

    @Transactional
    void deleteByUserUserId(int userId);

}
