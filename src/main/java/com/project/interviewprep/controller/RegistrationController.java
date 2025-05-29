package com.project.interviewprep.controller;

import com.project.interviewprep.dto.RegistrationDTO;
import com.project.interviewprep.model.Resume;
import com.project.interviewprep.model.User;
import com.project.interviewprep.repository.UserRepository;
import com.project.interviewprep.service.ResumeExtractionService;

import jakarta.servlet.http.HttpSession;

import com.project.interviewprep.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ResumeRepository resumeRepository;

    @Autowired
    ResumeExtractionService resumeExtractionService;

    @GetMapping("/step1")
    public String step1() {
        return "Register";
    }

    @PostMapping("/step1")
    public String step1Post(@ModelAttribute RegistrationDTO registrationDTO, HttpSession session) {
        // Save user info to session
        session.setAttribute("registration", registrationDTO);
        return "redirect:/register/step2";
    }

    @GetMapping("/step2")
    public String step2() {
        return "register2";
    }

    @PostMapping("/step2")
    public String step2Post(@ModelAttribute RegistrationDTO registrationDTO,
                             @RequestParam(required = false) MultipartFile resumeFile,
                             @RequestParam(required = false) Boolean manualInput,
                             HttpSession session) throws Exception {
    
        RegistrationDTO sessionData = (RegistrationDTO) session.getAttribute("registration");
        sessionData.setName(registrationDTO.getName());
    
        if (Boolean.TRUE.equals(manualInput)) {
            // Manual input
            sessionData.setName(registrationDTO.getName());
            sessionData.setEducation(registrationDTO.getEducation());
            sessionData.setExperience(registrationDTO.getExperience());
            sessionData.setSkills(registrationDTO.getSkills());
            sessionData.setCertifications(registrationDTO.getCertifications());
            sessionData.setProjects(registrationDTO.getProjects());
    
        } else if (resumeFile != null && !resumeFile.isEmpty()) {
            // Save file
            String filePath = saveFile(resumeFile);
            sessionData.setFilePath(filePath);
    
            // 🧠 Extract resume data using Spring AI
            Resume extracted = resumeExtractionService.extractResumeData(filePath);
            sessionData.setEducation(extracted.getEducation());
            sessionData.setExperience(extracted.getExperience());
            sessionData.setSkills(extracted.getSkills());
            sessionData.setCertifications(extracted.getCertifications());
            sessionData.setProjects(extracted.getProjects());
            sessionData.setResumeFileName(resumeFile.getOriginalFilename());
    
        } else {
            return "register2"; // No input given
        }
    
        session.setAttribute("registration", sessionData);
        return "redirect:/register/step3";
    }
    

    @GetMapping("/step3")
    public String step3(HttpSession session, Model model) {
        RegistrationDTO registrationDTO = (RegistrationDTO) session.getAttribute("registration");
        if (registrationDTO == null) {
            return "redirect:/register/step1";
        }
        model.addAttribute("registration", registrationDTO);
        return "register3";
    }

    @PostMapping("/submit")
    public String completeRegistration(HttpSession session) {

        RegistrationDTO registrationDTO = (RegistrationDTO) session.getAttribute("registration");
        if (registrationDTO == null) {
            return "redirect:/register/step1"; // or show error
        }

        // Save user and resume to database
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setName(registrationDTO.getName());
        user.setRole("USER");
        user.setRegisteredAt(LocalDateTime.now());
        
        userRepository.save(user);

        // Save resume
        Resume resume = new Resume();
        resume.setEducation(registrationDTO.getEducation());
        resume.setExperience(registrationDTO.getExperience());
        resume.setSkills(registrationDTO.getSkills());
        resume.setCertifications(registrationDTO.getCertifications());
        resume.setProjects(registrationDTO.getProjects());
        resume.setFilePath(registrationDTO.getFilePath());
        resume.setUser(user);
        resume.setUploadedAt(LocalDate.now());
        resume.setUpdatedAt(LocalDate.now());
        resumeRepository.save(resume);

        // Clear session after registration
        session.invalidate();

        return "redirect:/login"; // Redirect to login or success page
    }

    private String saveFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString() + ".pdf";
        Path path = Paths.get("uploads/resumes", fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        return path.toString();
    }
    
}
