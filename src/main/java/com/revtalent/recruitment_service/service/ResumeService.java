package com.revtalent.recruitment_service.service;

import com.revtalent.recruitment_service.model.Candidate;
import com.revtalent.recruitment_service.model.mongo.Resume;
import com.revtalent.recruitment_service.repository.CandidateRepository;
import com.revtalent.recruitment_service.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final CandidateRepository candidateRepository;
    private final com.revtalent.recruitment_service.feign.AiServiceClient aiServiceClient;

    // ── record for download response ──────────────────────────────────────────
    public record ResumeFile(byte[] data, String contentType, String filename) {}

    // ── Upload resume + link to candidate ────────────────────────────────────
    public Map<String, Object> uploadAndLink(MultipartFile file, Long candidateId) {
        try {
            // 1. Save file bytes into MongoDB
            Resume resume = Resume.builder()
                    .candidateId(candidateId)
                    .fileUrl(file.getOriginalFilename())
                    .fileData(file.getBytes())        // store actual bytes for download
                    .parsedText(extractText(file))
                    .build();

            Resume saved = resumeRepository.save(resume);

            // 2. Link mongo ID back to MySQL Candidate row
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));
            candidate.setResumeMongoId(saved.getId());
            candidateRepository.save(candidate);

            Map<String, Object> result = new HashMap<>();
            result.put("resumeId", saved.getId());
            result.put("candidateId", candidateId);
            result.put("filename", file.getOriginalFilename());
            result.put("status", "uploaded");
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload resume: " + e.getMessage(), e);
        }
    }

    // ── Analyze only (no candidate link) ─────────────────────────────────────
    public Map<String, Object> analyzeResume(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        result.put("filename", file.getOriginalFilename());
        result.put("size", file.getSize());
        
        try {
            String text = extractText(file);
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("resumeText", text);
            requestBody.put("jobDescription", "General assessment of candidate skills and qualifications.");
            
            Map<String, Object> aiResponse = aiServiceClient.screenResume(requestBody);
            
            result.put("status", "analyzed");
            result.put("aiSummary", aiResponse.get("response"));
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    // ── Get resume metadata for a candidate ──────────────────────────────────
    public Map<String, Object> getResumeByCandidate(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));

        String mongoId = candidate.getResumeMongoId();
        if (mongoId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("hasResume", false);
            return result;
        }

        Resume resume = resumeRepository.findById(mongoId)
                .orElseThrow(() -> new RuntimeException("Resume not found in MongoDB"));

        Map<String, Object> result = new HashMap<>();
        result.put("hasResume", true);
        result.put("resumeId", resume.getId());
        result.put("filename", resume.getFileUrl());
        result.put("candidateId", candidateId);
        return result;
    }

    // ── Download resume file bytes ────────────────────────────────────────────
    public ResumeFile downloadResume(Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));

        String mongoId = candidate.getResumeMongoId();
        if (mongoId == null) {
            throw new RuntimeException("No resume uploaded for candidate: " + candidateId);
        }

        Resume resume = resumeRepository.findById(mongoId)
                .orElseThrow(() -> new RuntimeException("Resume not found in MongoDB"));

        byte[] data = resume.getFileData() != null
                ? resume.getFileData()
                : new byte[0];

        String filename = resume.getFileUrl() != null ? resume.getFileUrl() : "resume.pdf";
        String contentType = filename.endsWith(".pdf") ? "application/pdf"
                : filename.endsWith(".docx") ? "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                : "application/octet-stream";

        return new ResumeFile(data, contentType, filename);
    }

    // ── Basic text extraction helper ──────────────────────────────────────────
    private String extractText(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length > 5 * 1024 * 1024) { // 5MB limit
                return "File too large for extraction";
            }
            
            String filename = file.getOriginalFilename();
            if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
                try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.Loader.loadPDF(bytes)) {
                    org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
                    return stripper.getText(document);
                }
            }
            
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Extraction error";
        }
    }
}