package com.revtalent.recruitment_service.repository;

import com.revtalent.recruitment_service.model.JobPosting;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    @EntityGraph(attributePaths = {"department", "createdBy", "createdBy.user"})
    List<JobPosting> findAll();
}