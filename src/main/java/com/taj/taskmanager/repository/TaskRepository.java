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

    List<Task> findByStatus(Task.Status status);
    List<Task> findByPriority(Task.Priority priority);
    List<Task> findByTitleContainingIgnoreCase(String search);
    List<Task> findByProjectId(Long projectId);

    Page<Task> findByStatus(Task.Status status, Pageable pageable);
    Page<Task> findByPriority(Task.Priority priority, Pageable pageable);
    Page<Task> findByTitleContainingIgnoreCase(String search, Pageable pageable);
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
}