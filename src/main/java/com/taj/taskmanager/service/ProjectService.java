package com.taj.taskmanager.service;

import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.ProjectRepository;
import com.taj.taskmanager.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GroqService groqService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, GroqService groqService) {
        this.projectRepository = projectRepository;
        this.groqService = groqService;
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
            throw new AccessDeniedException("Access denied");
        }

        return project;
    }

    @Transactional
    public Project updateProject(Long projectId, Project updatedProject) {
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new ProjectNotFoundException("Project does not exist"));

        if (!project.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new AccessDeniedException("Access denied");
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
            throw new AccessDeniedException("Access denied");
        }

        projectRepository.delete(project);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));

        if (!project.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new AccessDeniedException("Access denied");
        }
        
        return project.getTasks();
    }

    public String assessRisk(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));

        List<Task> tasks = project.getTasks();

        String prompt = buildPrompt(project, tasks);

        return groqService.chat(prompt);
    }

    // Helper for assessRisk
    private String buildPrompt(Project project, List<Task> tasks) {
        LocalDate today = LocalDate.now();

        long overdue = tasks.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today))
                .count();

        long highPriority = tasks.stream()
                .filter(t -> Task.Priority.HIGH.equals(t.getPriority()))
                .count();

        long completed = tasks.stream()
                .filter(t -> Task.Status.COMPLETED.equals(t.getStatus()))
                .count();

        return String.format("""
                You are a project risk assessment assistant. Analyze the following project and respond with:
                1. Risk Level: LOW, MEDIUM, or HIGH
                2. A 2-3 sentence explanation of why
                3. One actionable recommendation
                
                Project: %s
                Total tasks: %d
                Completed tasks: %d
                Overdue tasks: %d
                High priority tasks: %d
                """,
                project.getName(), tasks.size(), completed, overdue, highPriority);
    }
}
