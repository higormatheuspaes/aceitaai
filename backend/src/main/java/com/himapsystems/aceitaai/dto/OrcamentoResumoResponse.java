package com.himapsystems.aceitaai.dto;

import com.himapsystems.aceitaai.domain.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrcamentoResumoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        String descricao,
        BigDecimal total,
        StatusOrcamento status,
        LocalDateTime criadoEm,
        UUID linkSlug
) {}
