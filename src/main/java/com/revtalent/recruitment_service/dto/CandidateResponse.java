package com.revtalent.recruitment_service.dto;

import com.revtalent.recruitment_service.model.Candidate;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CandidateResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String githubUrl;
    private Candidate.Status status;
    private Long jobId;
    private String jobTitle;
    private String departmentName;
    private String resumeMongoId;
    private LocalDateTime interviewDate;
    private LocalDate offerDate;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private Long interviewerId;
    private String interviewerName;
}