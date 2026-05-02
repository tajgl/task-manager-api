package com.taj.taskmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String riskLevel;

    @Column(nullable = false, length = 2000)
    private String explanation;

    @Column(nullable = false, length = 2000)
    private String recommendation;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assessedAt;

    @PrePersist
    protected void onCreate() {
        this.assessedAt = LocalDateTime.now();
    }
}
