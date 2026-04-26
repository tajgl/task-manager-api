package com.taj.taskmanager.repository;

import com.taj.taskmanager.dto.TaskResponse;
import com.taj.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByOwner(String owner);
    Page<Task> findByOwner(String owner, Pageable pageable);

    List<Task> findByOwnerAndStatus(String owner, Task.Status status);
    Page<Task> findByOwnerAndStatus(String owner, Task.Status status, Pageable pageable);

    List<Task> findByOwnerAndPriority(String owner, Task.Priority priority);
    Page<Task> findByOwnerAndPriority(String owner, Task.Priority priority, Pageable pageable);

    List<Task> findByOwnerAndTitleContainingIgnoreCase(String owner, String title);
    Page<Task> findByOwnerAndTitleContainingIgnoreCase(String owner, String title, Pageable pageable);

    List<Task> findByOwnerAndProjectId(String owner, Long projectId);
    Page<Task> findByOwnerAndProjectId(String owner, Long projectId, Pageable pageable);
}