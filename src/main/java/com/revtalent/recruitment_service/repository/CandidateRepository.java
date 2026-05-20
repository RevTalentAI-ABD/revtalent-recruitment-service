package com.revtalent.recruitment_service.repository;

import com.revtalent.recruitment_service.model.Candidate;
import com.revtalent.recruitment_service.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByStatus(Candidate.Status status);
    List<Candidate> findByJobPosting(JobPosting jobPosting);
    List<Candidate> findByEmail(String email);
    List<Candidate> findByJobPosting_Id(Long jobId);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Candidate c JOIN FETCH c.jobPosting WHERE c.id = :id")
    java.util.Optional<Candidate> findByIdWithJobPosting(@org.springframework.data.repository.query.Param("id") Long id);
}