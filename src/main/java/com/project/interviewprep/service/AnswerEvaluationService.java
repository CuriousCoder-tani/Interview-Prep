package com.project.interviewprep.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.interviewprep.dto.AiFeedbackResult;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class AnswerEvaluationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.model.id}")
    private String modelId;

    public AiFeedbackResult evaluateAnswer(String question, String answer) {
        String url = "https://router.huggingface.co/novita/v3/openai/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "model", modelId,
                "stream", false,
                "messages", List.of(
                        Map.of("role", "user", "content", String.format(
                                "You are acting like a technical interviewer. Evaluate the candidate's answer to the interview question based on depth, clarity, sentence formation, and structure. Avoid grammar corrections like capitalization or spelling.\n\n"
                                        +
                                        "Give short feedback with 5 bullet points and an improved sample answer.\n" +
                                        "At the end, include a rating as:\nRating: X/10\n\n" +
                                        "Question: %s\nCandidate's Answer: %s",
                                question, answer))));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            String raw = response.getBody();
            System.out.println("Raw Response: " + raw);

            if (raw == null) {
                return new AiFeedbackResult("AI could not generate a response.", 0);
            }

            // Find the content section more reliably
            String contentMarker = "\"content\":\"";
            int contentStart = raw.indexOf(contentMarker);
            if (contentStart == -1) {
                return new AiFeedbackResult("Could not find content in AI response.", 0);
            }

            contentStart += contentMarker.length();
            int contentEnd = raw.indexOf("}", contentStart);
            if (contentEnd == -1) {
                return new AiFeedbackResult("Malformed content in AI response.", 0);
            }

            String content = raw.substring(contentStart, contentEnd);
            // Replace escaped characters
            content = content
                    .replace("\\r\\n", "<br/>") // Windows newlines
                    .replace("\\n", "<br/>") // Unix newlines
                    .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");

            System.out.println("Extracted Content: " + content);

            int ratingStart = content.indexOf("### Rating: ");
            if (ratingStart == -1) {
                return new AiFeedbackResult("Could not find rating in AI response.", 0);
            }

            ratingStart += 12; // length of "### Rating: "

            // Extract rating substring from content, not raw
            int ratingEnd = content.indexOf("\n", ratingStart);
            if (ratingEnd == -1) {
                ratingEnd = content.length();
            }

            String ratingPart = content.substring(ratingStart, ratingEnd).trim();

            // Split to get just the number before "/"
            String[] parts = ratingPart.split("/");
            int score = 0;
            try {
                score = Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                return new AiFeedbackResult("Invalid rating format in AI response.", 0);
            }

            System.out.println("Score:" + score);

            return new AiFeedbackResult(content, score);

        } catch (Exception e) {
            return new AiFeedbackResult("Error during evaluation: " + e.getMessage(), 0);
        }
    }
}
