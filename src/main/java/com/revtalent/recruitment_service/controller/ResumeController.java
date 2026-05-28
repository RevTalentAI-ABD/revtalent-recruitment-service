package com.revtalent.recruitment_service.controller;

import com.revtalent.recruitment_service.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor

public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAndLink(
            @RequestParam("file") MultipartFile file,
            @RequestParam("candidateId") Long candidateId) {

        Map<String, Object> result = resumeService.uploadAndLink(file, candidateId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeResume(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(resumeService.analyzeResume(file));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<Map<String, Object>> getResumeByCandidate(
            @PathVariable Long candidateId) {

        return ResponseEntity.ok(resumeService.getResumeByCandidate(candidateId));
    }
    @GetMapping("/candidate/{candidateId}/download")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long candidateId) {

        ResumeService.ResumeFile resumeFile = resumeService.downloadResume(candidateId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, resumeFile.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resumeFile.filename() + "\"")
                .body(resumeFile.data());
    }
}
