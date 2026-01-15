package com.taj.taskmanager.service;

import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(long id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Long taskId, String title, String description, Task.Priority priority, LocalDate dueDate, Task.Status status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task does not exist"));
        if (title != null && !title.isEmpty() && !Objects.equals(task.getTitle(), title)) {
            task.setTitle(title);
        }
        if (description != null && !description.isEmpty() && !Objects.equals(task.getDescription(), description)) {
            task.setDescription(description);
        }
        if (priority != null && !Objects.equals(task.getPriority(), priority)) {
            task.setPriority(priority);
        }
        if (dueDate != null && !Objects.equals(task.getDueDate(), dueDate)) {
            task.setDueDate(dueDate);
        }
        if (status != null && !Objects.equals(task.getStatus(), status)) {
            task.setStatus(status);
        }

        return task;
    }

    public void deleteTask(Long taskId) {
        if (taskRepository.findById(taskId).isEmpty()) {
            throw new IllegalStateException("Task with id: " + taskId + " does not exist");
        }
        taskRepository.deleteById(taskId);
    }
}
