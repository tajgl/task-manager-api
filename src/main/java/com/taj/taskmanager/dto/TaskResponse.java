package com.taj.taskmanager.dto;

import com.taj.taskmanager.model.Task;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
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
    @Setter
    @Getter
    public static class ProjectSummary {
        private Long id;
        private String name;

        public ProjectSummary() {
        }

        public ProjectSummary(Long id, String name) {
            this.id = id;
            this.name = name;
        }

    }
}
