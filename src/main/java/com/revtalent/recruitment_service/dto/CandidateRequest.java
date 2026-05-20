package com.revtalent.recruitment_service.dto;

import lombok.Data;

@Data
public class CandidateRequest {
    private Long jobId;
    private String name;
    private String email;
    private String phone;
    private String githubUrl;
}