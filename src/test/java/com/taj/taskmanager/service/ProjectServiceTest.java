package com.taj.taskmanager.service;

import com.taj.taskmanager.dto.CreateProjectRequest;
import com.taj.taskmanager.dto.ProjectResponse;
import com.taj.taskmanager.dto.RiskAssessmentResponse;
import com.taj.taskmanager.dto.UpdateProjectRequest;
import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.mapper.ProjectMapper;
import com.taj.taskmanager.mapper.TaskMapper;
import com.taj.taskmanager.model.Project;
import com.taj.taskmanager.model.RiskAssessment;
import com.taj.taskmanager.repository.ProjectRepository;
import com.taj.taskmanager.repository.RiskAssessmentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private GroqService groqService;

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectResponse projectResponse;
    private CreateProjectRequest createRequest;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth =  new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        createRequest = new CreateProjectRequest();
        createRequest.setProjectName("Website Redesign");

        project = new Project();
        project.setId(1L);
        project.setName("Website Redesign");

        projectResponse = new ProjectResponse();
        projectResponse.setId(1L);
        projectResponse.setName("Website Redesign");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createProject_shouldSetOwnerAndReturnResponse() {
        // ARRANGE
        when(projectMapper.toEntity(createRequest)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        // ACT
        ProjectResponse result = projectService.createProject(createRequest);

        // ASSERT
        assertThat(project.getOwner()).isEqualTo("testuser");
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Website Redesign");
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void getAllProjects_shouldReturnOnlyCurrentUsersProjects() {
        // ARRANGE
        when(projectRepository.findByOwner("testuser")).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        // ACT
        List<ProjectResponse> results = projectService.getAllProjects();

        // ASSERT
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Website Redesign");
        verify(projectRepository, times(1)).findByOwner("testuser");
    }

}
