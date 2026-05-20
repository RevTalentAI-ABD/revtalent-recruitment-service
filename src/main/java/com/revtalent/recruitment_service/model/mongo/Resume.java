package com.revtalent.recruitment_service.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    private String id;

    private Long candidateId;   // FK to MySQL Candidate.id

    private String fileUrl;     // original filename

    private byte[] fileData;    // actual file bytes stored in MongoDB

    private String parsedText;  // extracted text content

    private double[] embeddings; // for AI scoring (future)
}
