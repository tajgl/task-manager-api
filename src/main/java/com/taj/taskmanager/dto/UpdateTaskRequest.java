package com.taj.taskmanager.dto;

import com.taj.taskmanager.model.Task;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UpdateTaskRequest {

    @Size(max = 200, message = "Title must be less that 200 characters")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    private LocalDate dueDate;

    private Task.Priority priority;

    private Task.Status status;

    private Long projectId;
}
