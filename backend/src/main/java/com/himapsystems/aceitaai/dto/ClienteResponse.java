package com.himapsystems.aceitaai.dto;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        String nome,
        String whatsapp,
        String cpf,
        String email,
        String observacoesInternas,
        LocalDateTime criadoEm
) {}
