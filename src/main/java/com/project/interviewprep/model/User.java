package com.project.interviewprep.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(length = 50, unique = true)
    private String username;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String password;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 15, unique = true)
    private String phone;

    private LocalDateTime registeredAt;

    private String role;

    private LocalDateTime loginAt;

}
