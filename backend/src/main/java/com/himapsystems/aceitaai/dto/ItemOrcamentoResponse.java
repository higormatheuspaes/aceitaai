package com.himapsystems.aceitaai.dto;

import java.math.BigDecimal;

public record ItemOrcamentoResponse(
        Long id,
        String descricao,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal desconto,
        BigDecimal total
) {}
