package com.revtalent.recruitment_service.repository;

import com.revtalent.recruitment_service.model.mongo.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResumeRepository extends MongoRepository<Resume, String> {

    // find resumes by candidate
    List<Resume> findByCandidateId(Long candidateId);
}