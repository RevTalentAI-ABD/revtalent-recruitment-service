package com.revtalent.recruitment_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "job_posting",
        indexes = { @Index(name = "idx_job_status", columnList = "status") }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            foreignKey = @ForeignKey(name = "fk_posting_dept"))
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",
            foreignKey = @ForeignKey(name = "fk_posting_creator"))
    private Employee createdBy;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(nullable = false)
    private Integer vacancies = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "ENUM('OPEN','CLOSED','ON_HOLD') DEFAULT 'OPEN'")
    private Status status = Status.OPEN;

    @Column(name = "mongo_jd_id", length = 100)
    private String mongoJdId;

    @Column(name = "posted_on", nullable = false)
    private LocalDate postedOn;

    @Column(name = "closed_on")
    private LocalDate closedOn;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Candidate> candidates;

    @PrePersist
    protected void onCreate() {
        if (postedOn == null) postedOn = LocalDate.now();
    }

    public enum Status { OPEN, CLOSED, ON_HOLD }
}