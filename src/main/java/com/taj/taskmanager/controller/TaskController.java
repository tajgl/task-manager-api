package com.taj.taskmanager.controller;

import jakarta.validation.Valid;
import com.taj.taskmanager.service.TaskService;
import com.taj.taskmanager.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        return new ResponseEntity<>(taskService.createTask(task), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false) Task.Status status,
                                                  @RequestParam(required = false) Task.Priority priority,
                                                  @RequestParam(required = false) String search,
                                                  @RequestParam(required = false) Long projectId,
                                                  @RequestParam(required = false) String sortBy,
                                                  @RequestParam(required = false) String order) {

        if (status != null) {
            return new ResponseEntity<>(taskService.getTasksByStatus(status), HttpStatus.OK);
        }

        if (priority != null) {
            return new ResponseEntity<>(taskService.getTasksByPriority(priority), HttpStatus.OK);
        }

        if (search != null) {
            return new ResponseEntity<>(taskService.getTasksByTitleContainingIgnoreCase(search), HttpStatus.OK);
        }

        if (projectId != null) {
            return new ResponseEntity<>(taskService.getTasksByProjectId(projectId), HttpStatus.OK);
        }

        if (sortBy != null) {
            return new ResponseEntity<>(taskService.getAllTasksSorted(sortBy, order), HttpStatus.OK);
        }

        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    @GetMapping(path = "{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        return new ResponseEntity<>(taskService.getTaskById(taskId), HttpStatus.OK);
    }

    @PutMapping(path = "{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task updatedTask) {
        return new ResponseEntity<>(taskService.updateTask(taskId, updatedTask), HttpStatus.OK);
    }

    @DeleteMapping(path = "{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
