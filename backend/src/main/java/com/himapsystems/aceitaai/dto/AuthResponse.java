package com.himapsystems.aceitaai.dto;

public record AuthResponse(
        String token,
        Long autonomoId,
        String nomeNegocio
) {}
