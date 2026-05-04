package com.taj.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.taj.taskmanager.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"id", "title", "description", "dueDate", "priority", "status", "project", "createdAt"})
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private Task.Priority priority;
    private Task.Status status;
    private ProjectSummary project;     // Nested DTO for project

    // Nested DTO to avoid circular references
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectSummary {
        private Long id;
        private String name;

    }
}
