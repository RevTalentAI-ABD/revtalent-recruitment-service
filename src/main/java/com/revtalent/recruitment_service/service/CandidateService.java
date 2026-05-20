package com.revtalent.recruitment_service.service;

import com.revtalent.recruitment_service.dto.CandidateRequest;
import com.revtalent.recruitment_service.dto.CandidateResponse;
import com.revtalent.recruitment_service.model.Candidate;
import com.revtalent.recruitment_service.model.Employee;
import com.revtalent.recruitment_service.model.JobPosting;
import com.revtalent.recruitment_service.repository.CandidateRepository;
import com.revtalent.recruitment_service.repository.EmployeeRepository;
import com.revtalent.recruitment_service.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final JobPostingRepository jobPostingRepository;
    private final EmployeeRepository employeeRepository;

    // ── Mapper ────────────────────────────────────────────────────────────────
    private CandidateResponse toResponse(Candidate c) {
        JobPosting job = c.getJobPosting();
        Employee interviewer = c.getInterviewer();
        return CandidateResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .githubUrl(c.getGithubUrl())
                .status(c.getStatus())
                .jobId(job != null ? job.getId() : null)
                .jobTitle(job != null ? job.getTitle() : null)
                .departmentName(
                        job != null && job.getDepartment() != null
                                ? job.getDepartment().getName()
                                : null
                )
                .resumeMongoId(c.getResumeMongoId())
                .interviewDate(c.getInterviewDate())
                .offerDate(c.getOfferDate())
                .appliedAt(c.getAppliedAt())
                .updatedAt(c.getUpdatedAt())
                // interviewer fields — safe nulls
                .interviewerId(interviewer != null ? interviewer.getId() : null)
                .interviewerName(interviewer != null ? interviewer.getName() : null)
                .build();
    }

    // ── Add candidate to a job ────────────────────────────────────────────────
    @Transactional
    public CandidateResponse addCandidate(CandidateRequest req) {
        JobPosting job = jobPostingRepository.findById(req.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found: " + req.getJobId()));

        boolean exists = candidateRepository
                .findByEmail(req.getEmail())
                .stream()
                .anyMatch(c -> c.getJobPosting().getId().equals(req.getJobId()));

        if (exists) {
            throw new IllegalArgumentException("Candidate with this email already applied to this job.");
        }

        Candidate candidate = Candidate.builder()
                .jobPosting(job)
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .githubUrl(req.getGithubUrl())
                .status(Candidate.Status.APPLIED)
                .build();

        return toResponse(candidateRepository.save(candidate));
    }

    // ── Get all candidates for a job ──────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CandidateResponse> getCandidatesByJob(Long jobId) {
        return candidateRepository.findByJobPosting_Id(jobId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get all candidates ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Move candidate through pipeline (status only) ─────────────────────────
    @Transactional
    public CandidateResponse updateStatus(Long id, String status) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + id));
        try {
            candidate.setStatus(Candidate.Status.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status +
                    ". Valid: APPLIED, SCREENING, INTERVIEW, OFFERED, HIRED, REJECTED, WITHDRAWN");
        }
        return toResponse(candidateRepository.save(candidate));
    }

    // ── Schedule interview — sets interviewDate + interviewer + moves to INTERVIEW
    @Transactional
    public CandidateResponse scheduleInterview(Long id, LocalDateTime interviewDate, Long interviewerId) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + id));

        candidate.setStatus(Candidate.Status.INTERVIEW);
        candidate.setInterviewDate(interviewDate);

        if (interviewerId != null) {
            Employee interviewer = employeeRepository.findById(interviewerId)
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + interviewerId));
            candidate.setInterviewer(interviewer);
        }

        return toResponse(candidateRepository.save(candidate));
    }

    // ── Delete candidate ──────────────────────────────────────────────────────
    @Transactional
    public void deleteCandidate(Long id) {
        if (!candidateRepository.existsById(id)) {
            throw new RuntimeException("Candidate not found: " + id);
        }
        candidateRepository.deleteById(id);
    }

    // ── Get by email ──────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CandidateResponse> getByEmail(String email) {
        return candidateRepository.findByEmail(email)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
}