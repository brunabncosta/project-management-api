package com.brunacosta.projectmanagement.service;

import com.brunacosta.projectmanagement.dto.request.CreateEmployeeRequest;
import com.brunacosta.projectmanagement.dto.response.EmployeeResponse;
import com.brunacosta.projectmanagement.entity.Employee;
import com.brunacosta.projectmanagement.exception.ConflictException;
import com.brunacosta.projectmanagement.exception.ResourceNotFoundException;
import com.brunacosta.projectmanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        if (employeeRepository.existsByCpf(request.cpf())) {
            throw new ConflictException("CPF already registered");
        }

        if (employeeRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        Employee employee = new Employee();
        employee.setName(request.name());
        employee.setCpf(request.cpf());
        employee.setEmail(request.email());
        employee.setSalary(request.salary());

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        return employeeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found: " + id)
                );
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getCpf(),
                employee.getEmail(),
                employee.getSalary()
        );
    }
}