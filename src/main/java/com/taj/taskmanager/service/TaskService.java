package com.taj.taskmanager.service;

import com.taj.taskmanager.dto.CreateTaskRequest;
import com.taj.taskmanager.dto.PageResponse;
import com.taj.taskmanager.dto.TaskResponse;
import com.taj.taskmanager.dto.UpdateTaskRequest;
import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.exception.TaskNotFoundException;
import com.taj.taskmanager.mapper.TaskMapper;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.Task;
import com.taj.taskmanager.repository.ProjectRepository;
import com.taj.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = taskMapper.toEntity(request);

        //  Set defaults
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(Task.Priority.MEDIUM);
        }

        //  Check for existing project
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
            task.setProject(project);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getAllTasksPaginated(int page, int size, String sortBy, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, (sortBy != null) ? sortBy : "id");

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskRepository.findAll(pageable);

        List<TaskResponse> taskResponses = taskPage.getContent().stream().map(taskMapper::toResponse).toList();

        return new PageResponse<>(
                taskResponses,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isFirst(),
                taskPage.isLast()
                );
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));
        return taskMapper.toResponse(task);
    }

    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));

        taskMapper.updateEntity(task, request);

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
            task.setProject(project);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId) {
        if (taskRepository.findById(taskId).isPresent()) {
            taskRepository.deleteById(taskId);
        }
        else {
            throw new TaskNotFoundException("Task does not exist");
        }
    }

    //  Filtering
    public List<TaskResponse> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getTasksByStatusPaginated(Task.Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByStatus(status, pageable);

        List<TaskResponse> taskResponses = taskPage.getContent().stream().map(taskMapper::toResponse).toList();

        return new PageResponse<>(
                taskResponses,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isFirst(),
                taskPage.isLast()
                );
    }

    public List<TaskResponse> getTasksByPriority(Task.Priority priority) {
        return taskRepository.findByPriority(priority).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getTasksByPriorityPaginated(Task.Priority priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByPriority(priority, pageable);

        List<TaskResponse> taskResponses = taskPage.getContent().stream().map(taskMapper::toResponse).toList();

        return new PageResponse<>(
                taskResponses,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isFirst(),
                taskPage.isLast()
        );
    }

    public List<TaskResponse> getTasksByTitleContainingIgnoreCase(String search) {
        return taskRepository.findByTitleContainingIgnoreCase(search).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getTaskByTitleContainingIgnoreCasePaginated(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByTitleContainingIgnoreCase(search, pageable);

        List<TaskResponse> taskResponses = taskPage.getContent().stream().map(taskMapper::toResponse).toList();

        return new PageResponse<>(
                taskResponses,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isFirst(),
                taskPage.isLast()
        );
    }

    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getTasksByProjectIdPaginated(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByProjectId(projectId, pageable);

        List<TaskResponse> taskResponses = taskPage.getContent().stream().map(taskMapper::toResponse).toList();

        return new PageResponse<>(
                taskResponses,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isFirst(),
                taskPage.isLast()
        );
    }

    public List<TaskResponse> getAllTasksSorted(String sortBy, String order) {
        if("priority".equals(sortBy)) {
            List<TaskResponse> tasks = getAllTasks();

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

        return taskRepository.findAll(sort).stream().map(taskMapper::toResponse).toList();
    }

    //  Helper for priority sorting
    private int getPriorityValue(Task.Priority priority) {
        return switch (priority) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }
}
