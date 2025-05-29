package com.project.interviewprep.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import com.project.interviewprep.model.Answer;
import com.project.interviewprep.model.Feedback;
import com.project.interviewprep.model.Resume;
import com.project.interviewprep.model.User;
import com.project.interviewprep.repository.ResponseRepository;
import com.project.interviewprep.repository.AnswerRepository;
import com.project.interviewprep.repository.ResumeRepository;
import com.project.interviewprep.repository.UserRepository;
import com.project.interviewprep.repository.FeedbackRepository;
import com.project.interviewprep.service.AnswerEvaluationService;


@Controller
public class ProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ResumeRepository resumeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    AnswerEvaluationService answerEvaluationService;
       
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        Resume resume = resumeRepository.findByUser(user);
        if (resume != null) {
            model.addAttribute("resume", resume);
        } else {
            model.addAttribute("resume", new Resume());
        }

        return "profile";
    }

     @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                              @RequestParam String name,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String phone) {
        User userbysession = (User) session.getAttribute("user");
        if (userbysession == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(userbysession.getUsername());
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        userRepository.save(user);
        session.setAttribute("user", user); // Update the session with the new user details
        return "redirect:/profile";
    }
    
    @PostMapping("/change-password")
    public String changePassword(HttpSession session,
                                @RequestParam String currentPassword,
                                @RequestParam String newPassword) {
        // Implement password change logic
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            // Handle incorrect current password
            return "redirect:/profile?error=incorrect_password";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        session.setAttribute("user", user); // Update the session with the new password
        return "redirect:/logout";
    }
    
    @PostMapping("/resume")
    public String updateResume(@ModelAttribute Resume resume,HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Resume existingResume = resumeRepository.findByUser(user);        
        existingResume.setEducation(resume.getEducation());
        existingResume.setExperience(resume.getExperience());
        existingResume.setSkills(resume.getSkills());
        existingResume.setCertifications(resume.getCertifications());
        existingResume.setProjects(resume.getProjects());
        existingResume.setUser(user);
        existingResume.setUpdatedAt(LocalDate.now());
        
        resumeRepository.save(existingResume);
        return "redirect:/profile";
    }
    
    @PostMapping("/upload-resume")
    public String uploadResume(HttpSession session,
                             @RequestParam("resumeFile") MultipartFile file) throws IOException {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        if (!file.isEmpty()) {
            String uploadDir = "uploads/resumes/";
            String fileName = user.getUserId() + "_" + System.currentTimeMillis() + 
                             file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            Resume resume = resumeRepository.findByUser(user);
            
            resume.setFilePath("/" + uploadDir + fileName);
            resume.setUser(user);
            resume.setUploadedAt(LocalDate.now());
            resume.setUpdatedAt(LocalDate.now());
            
            resumeRepository.save(resume);
        }
        return "redirect:/profile";
    }

    @GetMapping ("/feedback")
    public String feedback(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam String feedbacktext, @RequestParam int rating, HttpSession session) {
        User user = (User) session.getAttribute("user");
        // Save feedback logic here
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setFeedbackText(feedbacktext);
        feedback.setRating(rating);
        feedback.setSubmittedAt(LocalDate.now());
        feedbackRepository.save(feedback);

        return "redirect:/profile";
    }

    @GetMapping("/question")
    public String answeredQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,HttpSession session ) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Answer> answerPage;


        
        if (category != null && !category.isEmpty()) {
            answerPage = answerRepository.findByUserIdAndQuestionIdCategory(user, category, pageable);
        } else {
            answerPage = answerRepository.findByUserId(user, pageable);
        }
        
        model.addAttribute("answers", answerPage.getContent());
        model.addAttribute("currentPage", answerPage.getNumber() + 1);
        model.addAttribute("totalPages", answerPage.getTotalPages());
        model.addAttribute("category", category);
        
        return "QuestionEvaluation";
    }

}
