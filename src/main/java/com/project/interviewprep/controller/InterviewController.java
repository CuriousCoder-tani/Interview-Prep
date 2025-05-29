package com.project.interviewprep.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.interviewprep.repository.AnswerRepository;
import com.project.interviewprep.repository.QuestionRepo;
import com.project.interviewprep.repository.UserRepository;
import com.project.interviewprep.repository.ResponseRepository;
import com.project.interviewprep.service.AnswerEvaluationService;
import com.project.interviewprep.repository.ResumeRepository;
import com.project.interviewprep.model.Response;
import com.project.interviewprep.model.User;
import com.project.interviewprep.model.Resume;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.ui.Model;
import java.time.LocalDateTime;

import com.project.interviewprep.dto.AiFeedbackResult;
import com.project.interviewprep.model.Answer;
import com.project.interviewprep.model.Question;

@Controller
public class InterviewController {

    @Autowired
    QuestionRepo questionRepository;

    @Autowired
    AnswerRepository answerRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ResponseRepository resRepo;

    @Autowired
    AnswerEvaluationService evaluationService;

    @Autowired
    ResumeRepository resumeRepo;

    @GetMapping("/interview")
    public String interview(Model model, HttpSession session) {
        List<String> category = questionRepository.findDistinctCategories();
        // Remove Introduction from the list
        category.remove("Introduction");
        model.addAttribute("categories", category);
        model.addAttribute("jobPosition", null);

        // If the user has not detail in resume table
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Resume resume = resumeRepo.findByUser(user);
            if (resume == null) {
                model.addAttribute("message", "Please fill your resume details first.");
            }

        } else {
            return "redirect:/login";
        }
        return "interview";
    }

    @GetMapping("/interview/{category}")
    public String interviewCategory(Model model, @PathVariable String category, HttpSession session) {
        // If the user is not in session logout
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        String experience = resumeRepo.findByUser((User) session.getAttribute("user")).getExperience();
        String difficulty = "null";
        int years = 0;
        if (experience == null || experience.equalsIgnoreCase("Fresher")) {
            difficulty = "easy";
        } else {
            try {
                years = Integer.parseInt(experience.split(" ")[0]);
                if (years <= 1)
                    difficulty = "easy";
                else if (years <= 3)
                    difficulty = "medium";
                else
                    difficulty = "hard";
            } catch (Exception e) {
                difficulty = "easy";
            }
        }

        List<Question> questions = questionRepository.findRandomQuestions(category, difficulty);
        List<Question> introductory = questionRepository.findRandomQuestions("Introduction", difficulty);
        // Add 5 questions from questions and 5 from introductory
        if (questions.size() > 5) {
            questions = questions.subList(0, 5);
        }
        if (introductory.size() > 5) {
            introductory = introductory.subList(0, 5);
        }

        // first add introductory then questions
        introductory.addAll(questions);
        session.setAttribute("InterviewQuestion", introductory);
        model.addAttribute("questions", introductory);
        model.addAttribute("currentquestion", introductory.get(0));
        model.addAttribute("jobPosition", category);
        model.addAttribute("index", 1);
        System.out.println("Category: " + category);
        return "interview";
    }

    @PostMapping("/submit-answer")
    public String submitAnswer(@RequestParam int questionId,
            @RequestParam String userAnswer,
            @RequestParam int currentIndex,
            @RequestParam String jobPosition,
            Model model, HttpSession session) {

        // If the user is not in session logout
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Save the answer
        Answer answer = new Answer();
        Question question = questionRepository.findById(questionId).orElse(null);
        answer.setQuestionId(question);
        answer.setAnswerText(userAnswer);
        User user = (User) session.getAttribute("user");
        answer.setUserId(user);
        answer.setAnswerDate(LocalDateTime.now());
        answerRepo.save(answer);

        // Evaluate using AI
        AiFeedbackResult aiResult = evaluationService.evaluateAnswer(question.getQuestionText(), userAnswer);

        // Save response
        Response response = new Response();
        response.setResponseText(aiResult.getFeedback());
        response.setAnswer(answer);
        response.setTotalScore(aiResult.getScore() * 10);
        resRepo.save(response);

        List<Question> intro = (List<Question>) session.getAttribute("InterviewQuestion");

        int nextIndex = currentIndex + 1;

        if (nextIndex >= intro.size()) {
            model.addAttribute("message", "Interview completed!");
            session.removeAttribute("interviewQuestions");
            return "redirect:/home";
        }

        model.addAttribute("questions", intro);
        model.addAttribute("currentquestion", intro.get(nextIndex));
        model.addAttribute("index", nextIndex + 1);
        model.addAttribute("jobPosition", jobPosition);
        model.addAttribute("currentIndex", nextIndex);

        return "interview";
    }

}
