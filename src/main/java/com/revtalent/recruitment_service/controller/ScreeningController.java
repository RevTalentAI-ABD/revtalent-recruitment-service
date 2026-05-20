package com.revtalent.recruitment_service.controller;

import com.revtalent.recruitment_service.dto.ScreeningResultDTO;
import com.revtalent.recruitment_service.service.ResumeScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
public class ScreeningController {

    private final ResumeScreeningService screeningService;

    // 🔹 Single candidate score
    @GetMapping("/{candidateId}")
    public double getScore(@PathVariable Long candidateId) {
        return screeningService.calculateMatchScore(candidateId);
    }

    // 🔥 Ranking for HR
    @GetMapping("/job/{jobId}")
    public List<ScreeningResultDTO> rankCandidates(@PathVariable Long jobId) {
        return screeningService.rankCandidates(jobId);
    }

    // 🤖 AI Candidate analysis
    @GetMapping("/{candidateId}/ai-analysis")
    public String getAiAnalysis(@PathVariable Long candidateId) {
        return screeningService.getAiResumeAnalysis(candidateId);
    }
}
