package com.taj.taskmanager.controller;

import com.taj.taskmanager.dto.CreateTaskRequest;
import com.taj.taskmanager.dto.TaskResponse;
import com.taj.taskmanager.dto.UpdateTaskRequest;
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
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return new ResponseEntity<>(taskService.createTask(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(@RequestParam(required = false) Task.Status status,
                                         @RequestParam(required = false) Task.Priority priority,
                                         @RequestParam(required = false) String search,
                                         @RequestParam(required = false) Long projectId,
                                         @RequestParam(required = false) String sortBy,
                                         @RequestParam(required = false) String order,
                                         @RequestParam(required = false) Integer page,      //  For pagination
                                         @RequestParam(required = false) Integer size) {    //  For pagination

        //  If pagination params provided
        if (page != null && size != null) {
            return new ResponseEntity<>(taskService.getALlTasksPaginated(page, size, sortBy, order), HttpStatus.OK);
        }

        //  If status param provided
        if (status != null) {
            return new ResponseEntity<>(taskService.getTasksByStatus(status), HttpStatus.OK);
        }

        //  If priority param provided
        if (priority != null) {
            return new ResponseEntity<>(taskService.getTasksByPriority(priority), HttpStatus.OK);
        }

        //  If search param provided
        if (search != null) {
            return new ResponseEntity<>(taskService.getTasksByTitleContainingIgnoreCase(search), HttpStatus.OK);
        }

        //  If projectId param provided
        if (projectId != null) {
            return new ResponseEntity<>(taskService.getTasksByProjectId(projectId), HttpStatus.OK);
        }

        //  If soring params provided
        if (sortBy != null) {
            return new ResponseEntity<>(taskService.getAllTasksSorted(sortBy, order), HttpStatus.OK);
        }

        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    @GetMapping(path = "{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long taskId) {
        return new ResponseEntity<>(taskService.getTaskById(taskId), HttpStatus.OK);
    }

    @PutMapping(path = "{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId, @RequestBody UpdateTaskRequest request) {
        return new ResponseEntity<>(taskService.updateTask(taskId, request), HttpStatus.OK);
    }

    @DeleteMapping(path = "{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
