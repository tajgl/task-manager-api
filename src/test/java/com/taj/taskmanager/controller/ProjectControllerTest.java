package com.taj.taskmanager.controller;

import com.taj.taskmanager.dto.*;
import com.taj.taskmanager.exception.ProjectNotFoundException;
import com.taj.taskmanager.security.JwtAuthenticationFilter;
import com.taj.taskmanager.security.JwtGenerator;
import com.taj.taskmanager.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtGenerator jwtGenerator;

    @MockitoBean
    private JwtAuthenticationFilter  jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectResponse projectResponse;
    private CreateProjectRequest createRequest;

    @BeforeEach
    public void setup() {
        createRequest = new CreateProjectRequest();
        createRequest.setProjectName("Website Redesign");
        createRequest.setDescription("Full redesign");

        projectResponse = new ProjectResponse();
        projectResponse.setId(1L);
        projectResponse.setName("Website Redesign");
    }

    //-------------------------------createProject--------------------------------

    @Test
    public void createProject_shouldReturn201AndProjectResponse() throws Exception {
        when(projectService.createProject(any(CreateProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Website Redesign"));
    }

    //-------------------------------getAllProjects--------------------------------

    @Test
    public void getAllProjects_shouldReturn200AndListOfProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(projectResponse));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Website Redesign"));
    }

    //-------------------------------getProjectById--------------------------------

    @Test
    public void getProjectById_shouldReturn200AndProjectResponse() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(projectResponse);

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Website Redesign"));
    }

    @Test
    public void getProjectById_shouldReturn404_whenProjectNotFound() throws Exception {
        when(projectService.getProjectById(99L)).thenThrow(new ProjectNotFoundException("Project does not exist"));

        mockMvc.perform(get("/api/projects/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getProjectById_shouldReturn403_whenAccessDenied() throws Exception {
        when(projectService.getProjectById(1L)).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    //-------------------------------updateProject--------------------------------

    @Test
    public void updateProject_shouldReturn200AndUpdatedResponse() throws Exception {
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setProjectName("Updated Name");

        when(projectService.updateProject(eq(1L), any(UpdateProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(put("/api/projects/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Website Redesign"));
    }

    @Test
    public void updateProject_shouldReturn404_whenProjectNotFound() throws Exception {
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setProjectName("Updated Name");

        when(projectService.updateProject(eq(99L), any(UpdateProjectRequest.class))).thenThrow(new ProjectNotFoundException("Project does not exist"));

        mockMvc.perform(put("/api/projects/{id}", 99L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProject_shouldReturn403_whenAccessDenied() throws Exception {
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setProjectName("Updated Name");

        when(projectService.updateProject(eq(1L), any(UpdateProjectRequest.class))).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(put("/api/projects/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    //-------------------------------deleteProject--------------------------------

    @Test
    public void deleteProject_shouldReturn204() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteProject_shouldReturn404_whenProjectNotFound() throws Exception {
        doThrow(new ProjectNotFoundException("Project does not exist")).when(projectService).deleteProject(99L);

        mockMvc.perform(delete("/api/projects/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteProject_shouldReturn403_whenAccessDenied() throws Exception {
        doThrow(new AccessDeniedException("Access denied")).when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/{id}", 1L))
                .andExpect(status().isForbidden());
    }


    //-------------------------------getTasksByProjectId--------------------------------

    @Test
    public void getTasksByProjectId_shouldReturn200AndListOfTasks() throws Exception {
        TaskResponse task = new TaskResponse();
        task.setId(1L);
        task.setTitle("Design mockups");
        when(projectService.getTasksByProjectId(1L)).thenReturn(List.of(task));

        mockMvc.perform(get("/api/projects/{id}/tasks", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Design mockups"));
    }

    @Test
    public void getTasksByProjectId_shouldReturn404_whenProjectNotFound() throws Exception {
        when(projectService.getTasksByProjectId(99L)).thenThrow(new ProjectNotFoundException("Project does not exist"));

        mockMvc.perform(get("/api/projects/{id}/tasks", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTasksByProjectId_shouldReturn403_whenAccessDenied() throws Exception {
        when(projectService.getTasksByProjectId(1L)).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/projects/{id}/tasks", 1L))
                .andExpect(status().isForbidden());
    }

    //-------------------------------getRiskAssessment--------------------------------

    @Test
    public void getRiskAssessment_shouldReturn200AndListOfRiskResponse() throws Exception {
        RiskAssessmentResponse riskAssessmentResponse = new RiskAssessmentResponse();
        riskAssessmentResponse.setRiskLevel("HIGH");
        riskAssessmentResponse.setId(1L);
        when(projectService.getRiskAssessment(1L)).thenReturn(riskAssessmentResponse);

        mockMvc.perform(get("/api/projects/{id}/risk-assessment", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void getRiskAssessment_shouldReturn404_whenProjectNotFound() throws Exception {
        when(projectService.getRiskAssessment(99L)).thenThrow(new ProjectNotFoundException("Project does not exist"));

        mockMvc.perform(get("/api/projects/{id}/risk-assessment", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRiskAssessment_shouldReturn403_whenAccessDenied() throws Exception {
        when(projectService.getRiskAssessment(1L)).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/projects/{id}/risk-assessment", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getRiskAssessmentHistory_shouldReturn200AndListOfRiskResponses() throws Exception {
        RiskAssessmentResponse riskAssessmentResponse = new RiskAssessmentResponse();
        riskAssessmentResponse.setRiskLevel("HIGH");
        riskAssessmentResponse.setId(1L);
        List<RiskAssessmentResponse> riskAssessmentHistory = List.of(riskAssessmentResponse);

        when(projectService.getRiskAssessmentHistory(1L)).thenReturn(riskAssessmentHistory);

        mockMvc.perform(get("/api/projects/{id}/risk-assessment/history", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].riskLevel").value("HIGH"))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void getRiskAssessmentHistory_shouldReturn404_whenProjectNotFound() throws Exception {
        when(projectService.getRiskAssessmentHistory(99L)).thenThrow(new ProjectNotFoundException("Project does not exist"));

        mockMvc.perform(get("/api/projects/{id}/risk-assessment/history", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRiskAssessmentHistory_shouldReturn403_whenAccessDenied() throws Exception {
        when(projectService.getRiskAssessmentHistory(1L)).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/projects/{id}/risk-assessment/history", 1L))
                .andExpect(status().isForbidden());
    }
}
