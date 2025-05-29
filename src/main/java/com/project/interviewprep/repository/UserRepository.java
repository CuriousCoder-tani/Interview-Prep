package com.project.interviewprep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.interviewprep.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);
    

}
