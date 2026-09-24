package com.brunacosta.projectmanagement.dto.response;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String name,
        String cpf,
        String email,
        BigDecimal salary
) {
}