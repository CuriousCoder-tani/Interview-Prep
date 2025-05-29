package com.project.interviewprep.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resume")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resumeId;
    
    @Column(length = 1000)
    private String education;

    @Column(length = 1000)
    private String experience;

    @Column(length = 1000)
    private String skills;

    @Column(length = 1000)
    private String certifications;

    @Column(length = 1000)
    private String projects;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(length = 250)
    private String filePath;

    private LocalDate uploadedAt;

    private LocalDate updatedAt;

}
