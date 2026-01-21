package com.taj.taskmanager.repository;

import com.taj.taskmanager.dto.TaskResponse;
import com.taj.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<TaskResponse> findByStatus(Task.Status status);

    List<TaskResponse> findByPriority(Task.Priority priority);

    List<TaskResponse> findByTitleContainingIgnoreCase(String search);

    List<TaskResponse> findByProjectId(Long projectId);
}