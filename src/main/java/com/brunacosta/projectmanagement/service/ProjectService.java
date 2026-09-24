package com.brunacosta.projectmanagement.service;

import com.brunacosta.projectmanagement.dto.request.CreateProjectRequest;
import com.brunacosta.projectmanagement.dto.response.EmployeeResponse;
import com.brunacosta.projectmanagement.dto.response.ProjectResponse;
import com.brunacosta.projectmanagement.entity.Employee;
import com.brunacosta.projectmanagement.entity.Project;
import com.brunacosta.projectmanagement.exception.ResourceNotFoundException;
import com.brunacosta.projectmanagement.repository.EmployeeRepository;
import com.brunacosta.projectmanagement.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            EmployeeRepository employeeRepository
    ) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        Set<Employee> employees = findEmployees(request.employeeIds());

        Project project = new Project();
        project.setName(request.name());
        project.setEmployees(employees);

        return toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return projectRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found: " + id)
                );
    }

    private Set<Employee> findEmployees(Set<Long> ids) {
        if (ids.isEmpty()) {
            return new HashSet<>();
        }

        List<Employee> employees = employeeRepository.findAllById(ids);

        if (employees.size() != ids.size()) {
            Set<Long> foundIds = employees.stream()
                    .map(Employee::getId)
                    .collect(java.util.stream.Collectors.toSet());

            Long missingId = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElseThrow();

            throw new ResourceNotFoundException(
                    "Employee not found: " + missingId
            );
        }

        return new HashSet<>(employees);
    }

    private ProjectResponse toResponse(Project project) {
        Set<EmployeeResponse> employees = project.getEmployees()
                .stream()
                .map(employee -> new EmployeeResponse(
                        employee.getId(),
                        employee.getName(),
                        employee.getCpf(),
                        employee.getEmail(),
                        employee.getSalary()
                ))
                .collect(java.util.stream.Collectors.toSet());

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getCreatedAt(),
                employees
        );
    }
}