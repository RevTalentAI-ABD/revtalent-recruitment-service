package com.revtalent.recruitment_service.service;

import com.revtalent.recruitment_service.dto.JobPostingResponse;
import com.revtalent.recruitment_service.dto.JobRequest;
import com.revtalent.recruitment_service.model.JobPosting;
import com.revtalent.recruitment_service.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final JobPostingRepository repo;

    private JobPostingResponse mapToResponse(JobPosting j) {
        JobPostingResponse res = new JobPostingResponse();
        res.setId(j.getId());
        res.setTitle(j.getTitle());
        res.setDescription(j.getDescription());
        res.setRequirements(j.getRequirements());
        res.setVacancies(j.getVacancies());
        res.setStatus(j.getStatus());
        res.setPostedOn(j.getPostedOn());
        res.setClosedOn(j.getClosedOn());


        if (j.getDepartment() != null) {
            res.setDepartmentName(j.getDepartment().getName());
        }
        if (j.getCreatedBy() != null && j.getCreatedBy().getUser() != null) {
            res.setCreatedByName(j.getCreatedBy().getUser().getName());
        }

        return res;
    }
    public List<JobPostingResponse> getAllJobs() {
        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public JobPostingResponse createJob(JobRequest req) {
        JobPosting j = new JobPosting();
        j.setTitle(req.getTitle());
        j.setDescription(req.getDescription());
        j.setRequirements(req.getRequirements());
        j.setVacancies(req.getVacancies());
        j.setStatus(JobPosting.Status.valueOf(req.getStatus()));
        return mapToResponse(repo.save(j));
    }
}