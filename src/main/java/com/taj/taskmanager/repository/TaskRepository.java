package com.taj.taskmanager.repository;

import com.taj.taskmanager.dto.TaskResponse;
import com.taj.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Task.Status status);

    List<Task> findByPriority(Task.Priority priority);

    List<Task> findByTitleContainingIgnoreCase(String search);

    List<Task> findByProjectId(Long projectId);
}