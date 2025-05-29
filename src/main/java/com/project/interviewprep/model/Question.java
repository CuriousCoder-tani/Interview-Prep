package com.project.interviewprep.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table (name = "questions")
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionId;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String questionText;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String difficultyLevel;

    @Column(length = 100)
    private String tokens;
}
