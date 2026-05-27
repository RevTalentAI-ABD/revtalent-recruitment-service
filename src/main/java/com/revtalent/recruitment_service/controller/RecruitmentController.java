package com.revtalent.recruitment_service.controller;

import com.revtalent.recruitment_service.dto.JobPostingResponse;
import com.revtalent.recruitment_service.dto.JobRequest;
import com.revtalent.recruitment_service.model.JobPosting;
import com.revtalent.recruitment_service.repository.JobPostingRepository;
import com.revtalent.recruitment_service.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasRole('HR_ADMIN')")
public class RecruitmentController {

    private final RecruitmentService service;
    private final JobPostingRepository jobPostingRepository;

    @GetMapping("/jobs")
    @org.springframework.security.access.prepost.PreAuthorize("permitAll()")
    public ResponseEntity<List<JobPostingResponse>> jobs() {
        return ResponseEntity.ok(service.getAllJobs());
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobPostingResponse> create(@RequestBody JobRequest req) {
        return ResponseEntity.ok(service.createJob(req));
    }

    @PutMapping("/jobs/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        JobPosting job = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        try {
            job.setStatus(JobPosting.Status.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        jobPostingRepository.save(job);

        return ResponseEntity.ok("Status updated");
    }
}


