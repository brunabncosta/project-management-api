package com.brunacosta.projectmanagement.service;

import com.brunacosta.projectmanagement.dto.request.CreateProjectRequest;
import com.brunacosta.projectmanagement.dto.response.ProjectResponse;
import com.brunacosta.projectmanagement.entity.Employee;
import com.brunacosta.projectmanagement.entity.Project;
import com.brunacosta.projectmanagement.exception.ResourceNotFoundException;
import com.brunacosta.projectmanagement.repository.EmployeeRepository;
import com.brunacosta.projectmanagement.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void shouldCreateProjectWithEmployees() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Ana Silva");

        when(employeeRepository.findAllById(Set.of(1L)))
                .thenReturn(List.of(employee));

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> {
                    Project project = invocation.getArgument(0);
                    project.setId(1L);
                    project.setCreatedAt(LocalDateTime.now());
                    return project;
                });

        ProjectResponse response = projectService.create(
                new CreateProjectRequest(
                        "Project Management API",
                        Set.of(1L)
                )
        );

        assertEquals(1L, response.id());
        assertEquals("Project Management API", response.name());
        assertEquals(1, response.employees().size());

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void shouldFailWhenEmployeeDoesNotExist() {
        when(employeeRepository.findAllById(Set.of(99L)))
                .thenReturn(List.of());

        assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.create(
                        new CreateProjectRequest(
                                "Project Management API",
                                Set.of(99L)
                        )
                )
        );

        verify(projectRepository, never()).save(any());
    }
}