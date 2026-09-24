package com.brunacosta.projectmanagement.controller;

import com.brunacosta.projectmanagement.dto.request.CreateProjectRequest;
import com.brunacosta.projectmanagement.dto.response.ProjectResponse;
import com.brunacosta.projectmanagement.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody CreateProjectRequest request
    ) {
        ProjectResponse response = projectService.create(request);

        return ResponseEntity
                .created(URI.create("/api/v1/projects/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> findAll() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }
}