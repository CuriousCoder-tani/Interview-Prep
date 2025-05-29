package com.project.interviewprep.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import com.project.interviewprep.repository.UserRepository;
import com.project.interviewprep.repository.AnswerRepository;
import com.project.interviewprep.repository.FeedbackRepository;
import com.project.interviewprep.repository.ResponseRepository;
import com.project.interviewprep.repository.ResumeRepository;
import com.project.interviewprep.repository.QuestionRepo;

import com.project.interviewprep.model.User;
import com.project.interviewprep.model.Feedback;
import com.project.interviewprep.model.Question;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepo questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    ResumeRepository resumeRepository;

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("usercount", userRepository.count());
        model.addAttribute("questioncount", questionRepository.count());
        model.addAttribute("Responsecount", responseRepository.count());
        model.addAttribute("Answercount", answerRepository.count());
        model.addAttribute("Feedbackcount", feedbackRepository.count());

        return "admin";
    }

    @GetMapping("/admin/question")
    public String questionPage(Model model) {
        model.addAttribute("questions", questionRepository.findAll());
        model.addAttribute("categories", questionRepository.findDistinctCategories());
        return "Question";
    }

    @PostMapping("/admin/question")
    public String addQuestion(@RequestParam String questionText, @RequestParam String category,
            @RequestParam String difficulty,
            @RequestParam String keywords) {
        Question newQuestion = new Question();
        newQuestion.setQuestionText(questionText);
        newQuestion.setCategory(category);
        newQuestion.setDifficultyLevel(difficulty);
        newQuestion.setTokens(keywords);
        questionRepository.save(newQuestion);
        return "redirect:/admin/question";
    }

    @GetMapping("/admin/question/delete")
    public String deleteQuestion(@RequestParam int questionId) {
        questionRepository.deleteById(questionId);
        return "redirect:/admin/question";
    }

    @GetMapping("/admin/feedback")
    public String feedbackPage(Model model) {
        List<Feedback> feedbacks = feedbackRepository.findAll();

        long totalFeedback = feedbacks.size();
        double averageRating = feedbacks.stream()
                .filter(f -> f.getRating() > 0)
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        long positiveCount = feedbacks.stream()
                .filter(f -> f.getRating() >= 4)
                .count();
        double positivePercentage = totalFeedback == 0 ? 0.0 : (positiveCount * 100.0) / totalFeedback;

        long featureRequestsCount = feedbacks.stream()
                .filter(f -> f.getFeedbackText() != null && f.getFeedbackText().toLowerCase().contains("feature"))
                .count();

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("totalFeedback", totalFeedback);
        model.addAttribute("averageRating", String.format("%.1f", averageRating));
        model.addAttribute("positiveFeedbackPercentage", (int) positivePercentage);
        model.addAttribute("featureRequestsCount", featureRequestsCount);
        model.addAttribute("statusOptions", List.of("New", "Responded", "Resolved"));

        return "AdminFeedback";
    }

    @PostMapping("/admin/feedback/{id}")
    public String respondToFeedback(@PathVariable("id") int feedbackId,
            @RequestParam("response") String response) {

        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback != null) {
            feedback.setResponseGiven(response);
            feedback.setResponseAt(LocalDate.now());
            feedback.setResponded(true);
            feedbackRepository.save(feedback);
        }

        return "redirect:/admin/feedback";
    }

    @GetMapping("/admin/feedback/delete")
    public String deleteFeedback(@RequestParam("id") int feedbackId) {
        feedbackRepository.deleteById(feedbackId);
        return "redirect:/admin/feedback";
    }

    @GetMapping("/user/delete")
    public String deleteUser(@RequestParam("id") int userId) {
        resumeRepository.deleteByUserUserId(userId);
        userRepository.deleteById(userId);
        return "redirect:/admin";
    }

    @GetMapping("/admin/logout")
    public String logout() {
        return "redirect:/login";
    }
}
