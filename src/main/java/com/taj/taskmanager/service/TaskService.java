package com.taj.taskmanager.service;

import com.taj.taskmanager.exception.TaskNotFoundException;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    public Task getTaskById(long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));
    }

    @Transactional
    public Task updateTask(Long taskId, Task updatedTask) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));
        if (updatedTask.getTitle() != null && !updatedTask.getTitle().isEmpty() && !Objects.equals(task.getTitle(), updatedTask.getTitle())) {
            task.setTitle(updatedTask.getTitle());
        }
        if (!Objects.equals(task.getDescription(), updatedTask.getDescription())) {
            task.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getPriority() != null && !Objects.equals(task.getPriority(), updatedTask.getPriority())) {
            task.setPriority(updatedTask.getPriority());
        }
        if (!Objects.equals(task.getDueDate(), updatedTask.getDueDate())) {
            task.setDueDate(updatedTask.getDueDate());
        }
        if (updatedTask.getStatus() != null && !Objects.equals(task.getStatus(), updatedTask.getStatus())) {
            task.setStatus(updatedTask.getStatus());
        }

        return task;
    }

    public void deleteTask(Long taskId) {
        if (taskRepository.findById(taskId).isPresent()) {
            taskRepository.deleteById(taskId);
        }
        else {
            throw new TaskNotFoundException("Task does not exist");
        }
    }
}
