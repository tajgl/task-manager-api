package com.taj.taskmanager.controller;

import com.taj.taskmanager.dto.*;
import jakarta.validation.Valid;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        return new ResponseEntity<>(projectService.createProject(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
    }

    @GetMapping(path="{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getProjectById(projectId), HttpStatus.OK);
    }

    @PutMapping(path="{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody UpdateProjectRequest request) {
        return new ResponseEntity<>(projectService.updateProject(projectId, request), HttpStatus.OK);
    }

    @DeleteMapping(path="{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path="{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getTasksByProjectId(projectId), HttpStatus.OK);
    }

    @GetMapping(path = "{projectId}/risk-assessment")
    public ResponseEntity<RiskAssessmentResponse> getRiskAssessment(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.assessRisk(projectId), HttpStatus.OK);
    }

    @GetMapping(path = "{projectId}/risk-assessment/history")
    public ResponseEntity<List<RiskAssessmentResponse>> getRiskAssessmentHistory(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getRiskAssessmentHistory(projectId), HttpStatus.OK);
    }
}
