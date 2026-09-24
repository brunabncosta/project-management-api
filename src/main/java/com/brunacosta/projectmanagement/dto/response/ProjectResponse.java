package com.brunacosta.projectmanagement.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record ProjectResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        Set<EmployeeResponse> employees
) {
}