package com.revtalent.recruitment_service.dto;

import lombok.Data;

@Data
public class JobRequest {
    private String title;
    private String description;
    private String requirements;
    private Integer vacancies;
    private String status;
}