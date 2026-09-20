package com.himapsystems.aceitaai.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank String whatsapp,
        String cpf,
        String email,
        String observacoesInternas
) {}
