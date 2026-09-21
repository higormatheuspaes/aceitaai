package com.himapsystems.aceitaai.dto;

import com.himapsystems.aceitaai.domain.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrcamentoPublicoResponse(
        String nomeNegocio,
        String localNegocio,
        String clienteNome,
        List<ItemPublicoResponse> itens,
        BigDecimal total,
        Integer validadeDias,
        LocalDateTime validaAte,
        String formaPagamento,
        String observacoes,
        LocalDateTime criadoEm,
        Integer versao,
        StatusOrcamento status,
        DecisaoPublicaResponse decisao,
        boolean visualizacaoDoDono
) {}
