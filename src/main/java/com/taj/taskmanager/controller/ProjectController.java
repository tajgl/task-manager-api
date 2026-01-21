package com.taj.taskmanager.controller;

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
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        return new ResponseEntity<>(projectService.createProject(project), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
    }

    @GetMapping(path="{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getProjectById(projectId), HttpStatus.OK);
    }

    @PutMapping(path="{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable Long projectId, @RequestBody Project project) {
        return new ResponseEntity<>(projectService.updateProject(projectId, project), HttpStatus.OK);
    }

    @DeleteMapping(path="{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path="{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksByProjectId(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getTasksByProjectId(projectId), HttpStatus.OK);
    }
}
