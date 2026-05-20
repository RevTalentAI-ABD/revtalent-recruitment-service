package com.revtalent.recruitment_service.dto;

import com.revtalent.recruitment_service.model.JobPosting;
import lombok.*;
import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor
public class JobPostingResponse {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private Integer vacancies;
    private JobPosting.Status status;
    private String departmentName;
    private String createdByName;
    private LocalDate postedOn;
    private LocalDate closedOn;
}