package com.revtalent.recruitment_service.controller;

import com.revtalent.recruitment_service.dto.CandidateRequest;
import com.revtalent.recruitment_service.dto.CandidateResponse;
import com.revtalent.recruitment_service.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {
    private final org.springframework.core.env.Environment env;

    @GetMapping("/env")
    public ResponseEntity<String> getEnv() {
        return ResponseEntity.ok(env.getProperty("spring.data.mongodb.uri") + " | " + env.getProperty("spring.mongodb.uri"));
    }

    private final CandidateService candidateService;

    // ── Add candidate to a job ────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<CandidateResponse> addCandidate(@RequestBody CandidateRequest req) {
        return ResponseEntity.ok(candidateService.addCandidate(req));
    }

    // ── Get all candidates for a specific job ─────────────────────────────────
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<CandidateResponse>> getCandidatesByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(candidateService.getCandidatesByJob(jobId));
    }

    // ── Get all candidates ────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<CandidateResponse>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    // ── Move candidate through pipeline stages ────────────────────────────────
    @PutMapping("/{id}/status")
    public ResponseEntity<CandidateResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(candidateService.updateStatus(id, status));
    }

    // ── Schedule interview ─────────────────────────────────────────────────────
    // Body: { "interviewDate": "2026-05-20T10:30:00", "interviewerId": 3 }
    // Moves candidate to INTERVIEW + saves date + assigns interviewer
    @PutMapping("/{id}/schedule")
    public ResponseEntity<CandidateResponse> scheduleInterview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        String dateStr = (String) body.get("interviewDate");
        if (dateStr == null || dateStr.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime interviewDate;
        try {
            interviewDate = LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        Long interviewerId = null;
        Object rawId = body.get("interviewerId");
        if (rawId instanceof Number) {
            interviewerId = ((Number) rawId).longValue();
        } else if (rawId instanceof String && !((String) rawId).isBlank()) {
            try { interviewerId = Long.parseLong((String) rawId); } catch (NumberFormatException ignored) {}
        }

        return ResponseEntity.ok(candidateService.scheduleInterview(id, interviewDate, interviewerId));
    }

    // ── Delete candidate ──────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok("Candidate removed successfully");
    }
}