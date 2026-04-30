package com.taj.taskmanager.mapper;

import com.taj.taskmanager.dto.CreateProjectRequest;
import com.taj.taskmanager.dto.ProjectResponse;
import com.taj.taskmanager.dto.UpdateProjectRequest;
import com.taj.taskmanager.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public Project toEntity(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getProjectName());
        project.setDescription(request.getDescription());
        return project;
    }

    public ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }

    public void updateEntity(Project project, UpdateProjectRequest request) {
        if(request.getProjectName() != null && !request.getProjectName().isEmpty()) {
            project.setName(request.getProjectName());
        }
        if(request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
    }
}
