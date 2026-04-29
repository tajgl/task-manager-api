package com.taj.taskmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taj.taskmanager.dto.RiskAssessmentResponse;
import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.ProjectRepository;
import com.taj.taskmanager.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final GroqService groqService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public RiskAssessmentResponse assessRisk(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));

        List<Task> tasks = project.getTasks();
        String prompt = buildPrompt(project, tasks);
        String aiResponse = groqService.chat(prompt);

        try {
            RiskAssessmentResponse response = objectMapper.readValue(aiResponse, RiskAssessmentResponse.class);
            response.setProjectId(projectId);
            response.setProjectName(project.getName());
            response.setAssessedAt(LocalDateTime.now());
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response" + aiResponse);
        }
    }

    // Helper for assessRisk
    private String buildPrompt(Project project, List<Task> tasks) {
        LocalDate today = LocalDate.now();

        long completed = tasks.stream()
                .filter(t -> Task.Status.COMPLETED.equals(t.getStatus()))
                .count();

        List<Task> incompleteTasks = tasks.stream()
                .filter(t -> !Task.Status.COMPLETED.equals(t.getStatus()))
                .toList();

        long overdue = incompleteTasks.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(today))
                .count();

        long highPriority = incompleteTasks.stream()
                .filter(t -> Task.Priority.HIGH.equals(t.getPriority()))
                .count();

        return String.format("""
                You are a project risk assessment assistant.Analyze the following project data and respond ONLY with a JSON object.
                No extra text, no markdown, no code blocks. Just raw JSON.
                Use exactly these fields:
                {
                "riskLevel": "LOW" | "MEDIUM" | "HIGH",
                "explanation": "2-3 sentence explanation",
                "recommendation": "one actionable recommendation"
                }
               \s
                Project: %s
                Total tasks: %d
                Completed tasks: %d
                Incomplete overdue tasks: %d
                Incomplete high priority tasks: %d
              \s""",
                project.getName(), tasks.size(), completed, overdue, highPriority);
    }
}
