package com.taj.taskmanager.service;

import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.exception.TaskNotFoundException;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.ProjectRepository;
import com.taj.taskmanager.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public Task createTask(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(Task.Priority.MEDIUM);
        }
        
        if (task.getProject() != null && task.getProject().getId() != null) {
            Project project = projectRepository.findById(task.getProject().getId()).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
            task.setProject(project);
        }

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));
    }

    @Transactional
    public Task updateTask(Long taskId, Task updatedTask) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));
        if (!Objects.equals(task.getTitle(), updatedTask.getTitle())) {
            task.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null && !Objects.equals(task.getDescription(), updatedTask.getDescription())) {
            task.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getPriority() != null && !Objects.equals(task.getPriority(), updatedTask.getPriority())) {
            task.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getDueDate() != null && !Objects.equals(task.getDueDate(), updatedTask.getDueDate())) {
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

    public List<Task> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority);
    }

    public List<Task> getTasksByTitleContainingIgnoreCase(String search) {
        return taskRepository.findByTitleContainingIgnoreCase(search);
    }

    public List<Task> getAllTasksSorted(String sortBy, String order) {
        if("priority".equals(sortBy)) {
            List<Task> tasks = taskRepository.findAll();

            tasks.sort((t1,t2) -> {
                int p1 = getPriorityValue(t1.getPriority());
                int p2 = getPriorityValue(t2.getPriority());

                if ("desc".equals(order)) {
                    return p1 - p2;
                }
                else {
                    return p2 - p1;
                }
            });

            return tasks;
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        return taskRepository.findAll(sort);
    }

    //Helper for priority sorting
    private int getPriorityValue(Task.Priority priority) {
        return switch (priority) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }
}
