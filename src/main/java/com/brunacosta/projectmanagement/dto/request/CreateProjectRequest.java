package com.brunacosta.projectmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateProjectRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        Set<Long> employeeIds
) {
}