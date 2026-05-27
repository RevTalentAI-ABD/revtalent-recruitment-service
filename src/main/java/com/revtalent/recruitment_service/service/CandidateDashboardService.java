package com.revtalent.recruitment_service.service;

import com.revtalent.recruitment_service.model.Candidate;
import com.revtalent.recruitment_service.model.Users;
import com.revtalent.recruitment_service.repository.CandidateRepository;
import com.revtalent.recruitment_service.util.SecurityUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateDashboardService {

    private final SecurityUserContext securityUserContext;
    private final CandidateRepository candidateRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getProfile(String principal) {
        Users users = securityUserContext.resolveCurrentUser(principal);

        return Map.of(
                "id",        users.getId(),
                "name",      users.getName() != null ? users.getName() : "",
                "email",     users.getEmail(),
                "username",  users.getUsername(),
                "role",      users.getRole().name(),
                "firstName", firstName(users.getName()),
                "lastName",  lastName(users.getName())
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getApplications(String principal) {
        Users users = securityUserContext.resolveCurrentUser(principal);

        return candidateRepository.findByEmail(users.getEmail())
                .stream().map(c -> {
                    Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("id",            c.getId());
                    m.put("jobId",         c.getJobPosting() != null ? c.getJobPosting().getId() : null);
                    m.put("jobTitle",      c.getJobPosting() != null ? c.getJobPosting().getTitle() : "");
                    m.put("department",    c.getJobPosting() != null && c.getJobPosting().getDepartment() != null
                            ? c.getJobPosting().getDepartment().getName() : "");
                    m.put("status",        c.getStatus().name());
                    m.put("appliedDate",   c.getAppliedAt());
                    m.put("phone",         c.getPhone());
                    m.put("interviewDate", c.getInterviewDate());
                    m.put("interviewerName", c.getInterviewer() != null ? c.getInterviewer().getName() : null);
                    return m;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUpcomingInterviews(String principal) {
        Users users = securityUserContext.resolveCurrentUser(principal);

        return candidateRepository.findByEmail(users.getEmail())
                .stream()
                .filter(c -> c.getStatus() == Candidate.Status.INTERVIEW
                        && c.getInterviewDate() != null)
                .map(c -> {
                    Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("id",              c.getId());
                    m.put("jobTitle",        c.getJobPosting() != null ? c.getJobPosting().getTitle() : "");
                    m.put("department",      c.getJobPosting() != null && c.getJobPosting().getDepartment() != null
                            ? c.getJobPosting().getDepartment().getName() : "");
                    m.put("round",           "Round 1");
                    m.put("interviewType",   "Technical Interview");
                    m.put("interviewDate",   c.getInterviewDate());
                    m.put("scheduledDate",   c.getInterviewDate().toLocalDate());
                    m.put("scheduledTime",   c.getInterviewDate().toLocalTime().toString());
                    m.put("interviewerName", c.getInterviewer() != null ? c.getInterviewer().getName() : null);
                    return m;
                }).collect(Collectors.toList());
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private String lastName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
}