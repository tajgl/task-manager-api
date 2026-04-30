package com.taj.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 100, message = "Project name must be less than 100 characters")
    private String projectName;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
