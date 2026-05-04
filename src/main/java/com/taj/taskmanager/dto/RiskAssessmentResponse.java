package com.taj.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "projectId", "projectName", "riskLevel", "explanation", "recommendation", "assessedAt"})
public class RiskAssessmentResponse {

    private Long id;
    private Long projectId;
    private String projectName;
    private String riskLevel;
    private String explanation;
    private String recommendation;
    private LocalDateTime assessedAt;
}
