package com.project.interviewprep.dto;

public class AiFeedbackResult {
    private String feedback;
    private int score;

    public AiFeedbackResult(String feedback, int rating) {
        this.feedback = feedback;
        this.score = rating;
    }

    public String getFeedback() { return feedback; }
    public int getScore() { return score; }
}
