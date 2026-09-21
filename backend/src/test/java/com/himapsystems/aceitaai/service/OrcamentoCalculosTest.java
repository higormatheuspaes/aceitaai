package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Autonomo;
import com.himapsystems.aceitaai.domain.Cliente;
import com.himapsystems.aceitaai.domain.ItemOrcamento;
import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.StatusOrcamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrcamentoCalculosTest {

    private static Orcamento orcamento(LocalDateTime criadoEm, Integer validadeDias, StatusOrcamento status) {
        Orcamento orcamento = Orcamento.builder()
                .id(1L)
                .autonomo(Autonomo.builder().nomeNegocio("Negocio").documento("12345678900").build())
                .cliente(Cliente.builder().nome("Cliente").build())
                .versao(1)
                .status(status)
                .validadeDias(validadeDias)
                .linkSlug(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .criadoEm(criadoEm)
                .build();
        orcamento.adicionarItem(item("Sessao", 12, "80.00", "0"));
        orcamento.adicionarItem(item("Avaliacao", 1, "100.00", "25.50"));
        return orcamento;
    }

    private static ItemOrcamento item(String descricao, int quantidade, String valor, String desconto) {
        return ItemOrcamento.builder()
                .descricao(descricao)
                .quantidade(quantidade)
                .valorUnitario(new BigDecimal(valor))
                .desconto(new BigDecimal(desconto))
                .build();
    }

    @Test
    void calculaTotalDoOrcamentoComDescontoPorItem() {
        Orcamento o = orcamento(LocalDateTime.now(), 7, StatusOrcamento.PENDENTE);
        assertEquals(new BigDecimal("1034.50"), OrcamentoCalculos.totalDoOrcamento(o));
        assertEquals(new BigDecimal("74.50"), OrcamentoCalculos.totalDoItem(o.getItens().get(1)));
    }

    @Test
    void pendenteComPrazoVencidoViraExpirado() {
        Orcamento vencido = orcamento(LocalDateTime.now().minusDays(8), 7, StatusOrcamento.PENDENTE);
        assertEquals(StatusOrcamento.EXPIRADO, OrcamentoCalculos.statusEfetivo(vencido));
    }

    @Test
    void pendenteDentroDoPrazoContinuaPendente() {
        Orcamento noPrazo = orcamento(LocalDateTime.now().minusDays(6), 7, StatusOrcamento.PENDENTE);
        assertEquals(StatusOrcamento.PENDENTE, OrcamentoCalculos.statusEfetivo(noPrazo));
    }

    @Test
    void semValidadeNuncaExpira() {
        Orcamento antigo = orcamento(LocalDateTime.now().minusYears(2), null, StatusOrcamento.PENDENTE);
        assertNull(OrcamentoCalculos.validaAte(antigo));
        assertEquals(StatusOrcamento.PENDENTE, OrcamentoCalculos.statusEfetivo(antigo));
    }

    @Test
    void orcamentoJaRespondidoNaoViraExpirado() {
        for (StatusOrcamento status : new StatusOrcamento[]{StatusOrcamento.ACEITO, StatusOrcamento.RECUSADO, StatusOrcamento.AJUSTE}) {
            Orcamento respondido = orcamento(LocalDateTime.now().minusDays(30), 7, status);
            assertEquals(status, OrcamentoCalculos.statusEfetivo(respondido));
        }
    }

    @Test
    void hashEDeterministicoEMudaComOConteudo() {
        LocalDateTime criadoEm = LocalDateTime.of(2026, 9, 20, 15, 51, 23);
        String hash = HashDocumento.calcular(orcamento(criadoEm, 7, StatusOrcamento.PENDENTE));

        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
        assertEquals(hash, HashDocumento.calcular(orcamento(criadoEm, 7, StatusOrcamento.PENDENTE)));

        Orcamento alterado = orcamento(criadoEm, 7, StatusOrcamento.PENDENTE);
        alterado.getItens().get(0).setValorUnitario(new BigDecimal("80.01"));
        assertNotEquals(hash, HashDocumento.calcular(alterado));

        assertNotEquals(hash, HashDocumento.calcular(orcamento(criadoEm, 15, StatusOrcamento.PENDENTE)));
    }

    @Test
    void codigoDoComprovanteUsaOsOitoPrimeirosCaracteresDoHash() {
        assertEquals("AC-0D5D5F92", HashDocumento.codigoDoComprovante("0d5d5f92b16b1234"));
    }
}
