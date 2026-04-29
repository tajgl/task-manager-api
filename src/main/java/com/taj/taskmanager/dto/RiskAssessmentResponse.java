package com.taj.taskmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RiskAssessmentResponse {

    private Long projectId;
    private String projectName;
    private String riskLevel;
    private String explanation;
    private String recommendation;
    private LocalDateTime assessedAt;
}
