package com.himapsystems.aceitaai.dto;

import com.himapsystems.aceitaai.domain.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrcamentoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        String clienteWhatsapp,
        Integer versao,
        StatusOrcamento status,
        Integer validadeDias,
        LocalDateTime validaAte,
        String formaPagamento,
        String observacoes,
        UUID linkSlug,
        LocalDateTime criadoEm,
        List<ItemOrcamentoResponse> itens,
        BigDecimal total
) {}
