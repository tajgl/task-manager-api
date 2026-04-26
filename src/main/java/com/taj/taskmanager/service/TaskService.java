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
import com.taj.taskmanager.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        task.setOwner(SecurityUtils.getCurrentUsername());

        //  Set defaults
        if (task.getStatus() == null) {
            task.setStatus(Task.Status.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(Task.Priority.MEDIUM);
        }

        //  Check for existing project
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
            task.setProject(project);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findByOwner(SecurityUtils.getCurrentUsername()).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getAllTasksPaginated(int page, int size, String sortBy, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, (sortBy != null) ? sortBy : "id");

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> taskPage = taskRepository.findByOwner(SecurityUtils.getCurrentUsername(), pageable);

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

        if (!task.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return taskMapper.toResponse(task);
    }

    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));

        if (!task.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        taskMapper.updateEntity(task, request);

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
            task.setProject(project);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task does not exist"));

        if (!task.getOwner().equals(SecurityUtils.getCurrentUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        taskRepository.delete(task);

    }

    //  Filtering
    public List<TaskResponse> getTasksByStatus(Task.Status status) {
        return taskRepository.findByOwnerAndStatus(SecurityUtils.getCurrentUsername(), status).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getTasksByStatusPaginated(Task.Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByOwnerAndStatus(SecurityUtils.getCurrentUsername(),status, pageable);

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
        return taskRepository.findByOwnerAndPriority(SecurityUtils.getCurrentUsername(), priority).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getTasksByPriorityPaginated(Task.Priority priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByOwnerAndPriority(SecurityUtils.getCurrentUsername(), priority, pageable);

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
        return taskRepository
                .findByOwnerAndTitleContainingIgnoreCase(SecurityUtils.getCurrentUsername(), search)
                .stream().map(taskMapper::toResponse).
                toList();
    }

    public PageResponse<TaskResponse> getTaskByTitleContainingIgnoreCasePaginated(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByOwnerAndTitleContainingIgnoreCase(SecurityUtils.getCurrentUsername(), search, pageable);

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
        return taskRepository.findByOwnerAndProjectId(SecurityUtils.getCurrentUsername(), projectId).stream().map(taskMapper::toResponse).toList();
    }

    public PageResponse<TaskResponse> getTasksByProjectIdPaginated(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByOwnerAndProjectId(SecurityUtils.getCurrentUsername(), projectId, pageable);

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
