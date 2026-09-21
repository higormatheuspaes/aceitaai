package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.ItemOrcamento;
import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.StatusOrcamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public final class OrcamentoCalculos {

    private OrcamentoCalculos() {
    }

    public static BigDecimal subtotalBruto(int quantidade, BigDecimal valorUnitario) {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public static BigDecimal totalDoItem(ItemOrcamento item) {
        return subtotalBruto(item.getQuantidade(), item.getValorUnitario())
                .subtract(item.getDesconto())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal totalDoOrcamento(Orcamento orcamento) {
        return orcamento.getItens().stream()
                .map(OrcamentoCalculos::totalDoItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static LocalDateTime validaAte(Orcamento orcamento) {
        return orcamento.getValidadeDias() == null
                ? null
                : orcamento.getCriadoEm().plusDays(orcamento.getValidadeDias());
    }

    public static StatusOrcamento statusEfetivo(Orcamento orcamento) {
        LocalDateTime limite = validaAte(orcamento);
        boolean venceu = orcamento.getStatus() == StatusOrcamento.PENDENTE
                && limite != null
                && LocalDateTime.now().isAfter(limite);
        return venceu ? StatusOrcamento.EXPIRADO : orcamento.getStatus();
    }
}
