package com.taj.taskmanager.repository;

import com.taj.taskmanager.model.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    List<RiskAssessment> findByProjectIdOrderByAssessedAtDesc(Long projectId);
}
