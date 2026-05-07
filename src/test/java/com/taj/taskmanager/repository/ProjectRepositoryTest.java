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
    void findByOwner_shouldReturnOnlyProjectsForThatOwner() {
        // ARRANGE
        Project otherProject = new Project();
        otherProject.setName("Mobile App");
        otherProject.setDescription("iOS and Android app");
        otherProject.setOwner("otheruser");
        projectRepository.save(otherProject);

        // ACT
        List<Project> projects = projectRepository.findByOwner("testuser");

        // ARRANGE
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getName()).isEqualTo("Website Redesign");
        assertThat(projects.get(0).getOwner()).isEqualTo("testuser");
    }

    @Test
    void findByOwner_shouldReturnEmpty_whenOwnerHasNoProjects() {
        // ACT
        List<Project> results = projectRepository.findByOwner("nobody");

        // ASSERT — empty list, not null, not an exception
        assertThat(results).isEmpty();
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
