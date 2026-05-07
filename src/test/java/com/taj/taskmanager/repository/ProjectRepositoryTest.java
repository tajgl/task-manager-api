package com.taj.taskmanager.repository;

import com.taj.taskmanager.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    private Project project;

    @BeforeEach
    void setUp() {
        // ARRANGE
        taskRepository.deleteAll();
        projectRepository.deleteAll();

        project = new Project();
        project.setName("Website Redesign");
        project.setDescription("Full redesign of the company website");
        project.setOwner("testuser");
        project = projectRepository.save(project);
    }

    @Test
    void save_shouldPersistProject() {
        // ACT - saving already happened in setUp

        // ASSERT
        assertThat(project.getId()).isNotNull();
        assertThat(project.getName()).isEqualTo("Website Redesign");
        assertThat(project.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_shouldReturnProject_whenExists() {
        // ACT
        Optional<Project> found = projectRepository.findById(project.getId());

        // ASSERT
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Website Redesign");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        // ACT
        Optional<Project> found = projectRepository.findById(999L);

        // ASSERT
        assertThat(found).isEmpty();
    }

    @Test
    void findALl_shouldReturnAllProjects() {
        // ARRANGE
        Project second = new Project();
        second.setName("Mobile App");
        second.setOwner("testuser");
        projectRepository.save(second);

        //ACT
        List<Project> projects = projectRepository.findAll();

        // ASSERT
        assertThat(projects).hasSize(2);
    }

    @Test
    void delete_shouldDeleteProject() {
        // ACT
        projectRepository.deleteById(project.getId());

        // ASSERT
        Optional<Project> found = projectRepository.findById(project.getId());
        assertThat(found).isEmpty();
    }
}
