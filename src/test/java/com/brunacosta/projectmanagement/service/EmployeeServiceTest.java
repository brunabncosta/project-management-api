package com.brunacosta.projectmanagement.service;

import com.brunacosta.projectmanagement.dto.request.CreateEmployeeRequest;
import com.brunacosta.projectmanagement.dto.response.EmployeeResponse;
import com.brunacosta.projectmanagement.entity.Employee;
import com.brunacosta.projectmanagement.exception.ConflictException;
import com.brunacosta.projectmanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private CreateEmployeeRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateEmployeeRequest(
                "Ana Silva",
                "12345678901",
                "ana@example.com",
                new BigDecimal("15000.00")
        );
    }

    @Test
    void shouldCreateEmployee() {
        when(employeeRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(employeeRepository.existsByEmail(request.email())).thenReturn(false);

        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> {
                    Employee employee = invocation.getArgument(0);
                    employee.setId(1L);
                    return employee;
                });

        EmployeeResponse response = employeeService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Ana Silva", response.name());
        assertEquals("12345678901", response.cpf());
        assertEquals(new BigDecimal("15000.00"), response.salary());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldRejectDuplicatedCpf() {
        when(employeeRepository.existsByCpf(request.cpf())).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> employeeService.create(request)
        );

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        when(employeeRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(employeeRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> employeeService.create(request)
        );

        verify(employeeRepository, never()).save(any());
    }
}