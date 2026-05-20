package com.revtalent.recruitment_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_candidate_job", columnNames = {"job_id", "email"})
        },
        indexes = {
                @Index(name = "idx_candidate_status", columnList = "status"),
                @Index(name = "idx_candidate_interviewer", columnList = "interviewer_id")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_candidate_job"))
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id",
            foreignKey = @ForeignKey(name = "fk_candidate_itvwr"))
    private Employee interviewer;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "ENUM('APPLIED','SCREENING','INTERVIEW','OFFERED','HIRED','REJECTED','WITHDRAWN') DEFAULT 'APPLIED'")
    private Status status = Status.APPLIED;

    @Column(name = "resume_mongo_id", length = 100)
    private String resumeMongoId;

    @Column(name = "ai_score")
    private Double aiScore;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "interview_date")
    private LocalDateTime interviewDate;

    @Column(name = "offer_date")
    private LocalDate offerDate;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum Status {
        APPLIED, SCREENING, INTERVIEW, OFFERED, HIRED, REJECTED, WITHDRAWN
    }
}