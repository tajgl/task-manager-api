package com.taj.taskmanager.controller;

import com.taj.taskmanager.service.TaskService;
import com.taj.taskmanager.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping(path = "{taskId}")
    public Optional<Task> getTaskById(@PathVariable("taskId") Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @PutMapping(path = "{taskId}")
    public Task updateTask(@PathVariable("taskId") Long taskId,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String description,
                           @RequestParam(required = false) Task.Priority priority,
                           @RequestParam(required = false) LocalDate dueDate,
                           @RequestParam(required = false) Task.Status status) {
        return taskService.updateTask(taskId, title, description, priority, dueDate, status);
    }

    @DeleteMapping(path = "{taskId}")
    public void deleteTask(@PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
    }
}
