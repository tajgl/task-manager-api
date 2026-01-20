package com.taj.taskmanager.service;

import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (project.getName() == null) {
            throw new IllegalArgumentException("Project name required");
        }

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(()-> new ProjectNotFoundException("Project does not exist"));
    }

    @Transactional
    public Project updateProject(Long projectId, Project updatedProject) {
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new ProjectNotFoundException("Project does not exist"));

        if (updatedProject.getName() != null && !updatedProject.getName().isEmpty() && !Objects.equals(project.getName(), updatedProject.getName())) {
            project.setName(updatedProject.getName());
        }

        if (updatedProject.getDescription() != null && !Objects.equals(project.getDescription(), updatedProject.getDescription())) {
            project.setDescription(updatedProject.getDescription());
        }

        return project;
    }

    public void deleteProject(Long projectId) {
        if(projectRepository.findById(projectId).isPresent()) {
            projectRepository.deleteById(projectId);
        }
        else {
            throw new ProjectNotFoundException("Project does not exist");
        }
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));
        
        return project.getTasks();
    }
}
