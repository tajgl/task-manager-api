package com.taj.taskmanager.service;

import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.ProjectRepository;
import com.taj.taskmanager.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(Project project) {
        project.setOwner(SecurityUtils.getCurrentUsername());
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findByOwner(SecurityUtils.getCurrentUsername());
    }

    public Project getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()-> new ProjectNotFoundException("Project does not exist"));

        if (!project.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return project;
    }

    @Transactional
    public Project updateProject(Long projectId, Project updatedProject) {
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new ProjectNotFoundException("Project does not exist"));

        if (!project.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (updatedProject.getName() != null && !updatedProject.getName().isEmpty() && !Objects.equals(project.getName(), updatedProject.getName())) {
            project.setName(updatedProject.getName());
        }

        if (updatedProject.getDescription() != null && !Objects.equals(project.getDescription(), updatedProject.getDescription())) {
            project.setDescription(updatedProject.getDescription());
        }

        return project;
    }

    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));

        if (!project.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        projectRepository.delete(project);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));

        if (!project.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        
        return project.getTasks();
    }
}
