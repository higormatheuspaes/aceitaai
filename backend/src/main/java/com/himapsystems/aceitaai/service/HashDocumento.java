package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.ItemOrcamento;
import com.himapsystems.aceitaai.domain.Orcamento;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Impressao digital do conteudo do orcamento no momento da decisao. A serializacao e versionada ("v1")
 * e deterministica: qualquer mudanca no conteudo muda o hash.
 */
public final class HashDocumento {

    private HashDocumento() {
    }

    public static String calcular(Orcamento orcamento) {
        StringBuilder texto = new StringBuilder("v1\n");
        texto.append("orcamento=").append(orcamento.getId()).append('\n');
        texto.append("versao=").append(orcamento.getVersao()).append('\n');
        texto.append("slug=").append(orcamento.getLinkSlug()).append('\n');
        texto.append("negocio=").append(orcamento.getAutonomo().getNomeNegocio()).append('\n');
        texto.append("documento=").append(orcamento.getAutonomo().getDocumento()).append('\n');
        texto.append("cliente=").append(orcamento.getCliente().getNome()).append('\n');
        texto.append("criadoEm=").append(orcamento.getCriadoEm()).append('\n');
        texto.append("validadeDias=").append(orcamento.getValidadeDias()).append('\n');
        texto.append("pagamento=").append(orcamento.getFormaPagamento()).append('\n');
        texto.append("observacoes=").append(orcamento.getObservacoes()).append('\n');

        for (ItemOrcamento item : orcamento.getItens()) {
            texto.append("item=")
                    .append(item.getDescricao()).append(';')
                    .append(item.getQuantidade()).append(';')
                    .append(item.getValorUnitario().toPlainString()).append(';')
                    .append(item.getDesconto().toPlainString()).append('\n');
        }
        texto.append("total=").append(OrcamentoCalculos.totalDoOrcamento(orcamento).toPlainString());

        try {
            byte[] resumo = MessageDigest.getInstance("SHA-256").digest(texto.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumo);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel", e);
        }
    }

    public static String codigoDoComprovante(String hash) {
        return "AC-" + hash.substring(0, 8).toUpperCase();
    }
}
