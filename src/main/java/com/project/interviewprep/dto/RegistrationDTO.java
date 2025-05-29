package com.project.interviewprep.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDTO {

    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String name;
    private String profilePic;
    private String education;
    private String experience;
    private String skills;
    private String resumeFileName;
    private String filePath;
    private String certifications;
    private String projects;
    private Boolean manualInput;

}
