package com.taj.taskmanager.service;

import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return null;
    }

    public List<Task> getAllTasks() {
        return null;
    }

    public Task getTaskById(long id) {
        return null;
    }

    public Task updateTask(Long taskId, Task task) {
        return null;
    }

    public void deleteTask(Long taskId) {
    }
}
