package com.project.interviewprep.service;

import com.project.interviewprep.model.Resume;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeExtractionService {

    public Resume extractResumeData(String filePath) {
        try {
            FileSystemResource resource = new FileSystemResource(filePath);
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> docs = reader.get();

            if (docs == null || docs.isEmpty()) {
                throw new IllegalStateException("No content found in PDF");
            }

            StringBuilder fullText = new StringBuilder();
            for (Document doc : docs) {
                fullText.append(doc.getFormattedContent()).append("\n");
            }

            String text = fullText.toString();

            // Optional: Print to verify what's extracted
            System.out.println("Extracted resume text:");
            System.out.println(text);

            Resume resume = new Resume();
            resume.setEducation(findSection(text, "Education"));
            resume.setExperience(findSection(text, "Experience"));
            resume.setSkills(findSection(text, "Skills"));
            resume.setCertifications(findSection(text, "Certifications|CERTIF ICATE S"));
            resume.setProjects(findSection(text, "PROJECTS"));

            return resume;

        } catch (Exception e) {
            throw new RuntimeException("Error while reading PDF: " + e.getMessage(), e);
        }
    }

    private static final String[] SECTION_HEADERS = {
    "SUMMARY", "EDUCATION", "CERTIFICATES", "CERTIFICATE", "PROJECTS", "SKILLS", "EXPERIENCE"
};

private String findSection(String text, String section) {
    String normalizedText = text.replaceAll("\r", ""); // Normalize line endings
    StringBuilder regexBuilder = new StringBuilder();

    // Build regex like (?i)SUMMARY(.*?)(?=(EDUCATION|PROJECTS|...))
    regexBuilder.append("(?i)").append(section).append("\\s*\\n+");

    regexBuilder.append("(.*?)\\n+(?=(");
    for (int i = 0; i < SECTION_HEADERS.length; i++) {
        if (!SECTION_HEADERS[i].equalsIgnoreCase(section)) {
            regexBuilder.append(SECTION_HEADERS[i]);
            if (i < SECTION_HEADERS.length - 1) {
                regexBuilder.append("|");
            }
        }
    }
    regexBuilder.append(")|\\z)");

    Pattern pattern = Pattern.compile(regexBuilder.toString(), Pattern.DOTALL);
    Matcher matcher = pattern.matcher(normalizedText);

    if (matcher.find()) {
        return matcher.group(1).trim();
    }
    return "";
}

    
}
