package com.project.interviewprep.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Return the name of the login view (e.g., login.html or login.jsp)
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout"; // Redirect to the login page after logout

    }

}
