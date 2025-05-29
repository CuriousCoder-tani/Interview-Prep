package com.project.interviewprep.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.interviewprep.model.Question;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface QuestionRepo extends JpaRepository<Question, Integer>{

    List<Question> findByCategory(String category);

    @Query(value = "SELECT * FROM questions WHERE category = :category AND difficulty_level = :difficulty ORDER BY RAND()", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("category") String category, @Param("difficulty") String difficulty);

    @Query(value = "SELECT DISTINCT category FROM questions", nativeQuery = true)
    List<String> findDistinctCategories();


}
