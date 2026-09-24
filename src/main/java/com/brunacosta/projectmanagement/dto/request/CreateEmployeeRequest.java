package com.brunacosta.projectmanagement.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateEmployeeRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Pattern(regexp = "\\d{11}", message = "CPF must contain 11 digits")
        String cpf,

        @NotBlank
        @Email
        String email,

        @NotNull
        @PositiveOrZero
        BigDecimal salary
) {
}