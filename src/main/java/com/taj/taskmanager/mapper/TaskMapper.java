package com.taj.taskmanager.mapper;

import com.taj.taskmanager.dto.CreateTaskRequest;
import com.taj.taskmanager.dto.TaskResponse;
import com.taj.taskmanager.dto.UpdateTaskRequest;
import com.taj.taskmanager.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    // Convert CreateTaskRequest to Task entity
    public Task toEntity(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());

        return task;
    }

    // Convert Task entity to TaskResponse
    public TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setCreatedAt(task.getCreatedAt());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());

        // Map project to ProjectSummary if exists
        if (task.getProject() != null) {
            TaskResponse.ProjectSummary projectSummary = new TaskResponse.ProjectSummary(task.getProject().getId(), task.getProject().getName());
            response.setProject(projectSummary);
        }

        return response;
    }

    // Update existing Task entity from UpdateTaskRequest
    public void updateEntity(Task task, UpdateTaskRequest request) {
        if(request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle());
        }
        if(request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if(request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if(request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if(request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
    }
}
