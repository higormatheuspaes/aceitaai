package com.himapsystems.aceitaai.dto;

import java.math.BigDecimal;

public record ItemPublicoResponse(
        String descricao,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal desconto,
        BigDecimal total
) {}
