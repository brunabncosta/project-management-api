package com.brunacosta.projectmanagement.controller;

import com.brunacosta.projectmanagement.dto.request.CreateEmployeeRequest;
import com.brunacosta.projectmanagement.dto.response.EmployeeResponse;
import com.brunacosta.projectmanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody CreateEmployeeRequest request
    ) {
        EmployeeResponse response = employeeService.create(request);

        return ResponseEntity
                .created(URI.create("/api/v1/employees/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }
}