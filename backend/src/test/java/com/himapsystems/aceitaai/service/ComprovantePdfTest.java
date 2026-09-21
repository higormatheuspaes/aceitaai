package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Aceite;
import com.himapsystems.aceitaai.domain.Autonomo;
import com.himapsystems.aceitaai.domain.Cliente;
import com.himapsystems.aceitaai.domain.ItemOrcamento;
import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.StatusOrcamento;
import com.himapsystems.aceitaai.domain.TipoDecisao;
import org.junit.jupiter.api.Test;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComprovantePdfTest {

    private static final String HASH = "ab12cd34ef56ab12cd34ef56ab12cd34ef56ab12cd34ef56ab12cd34ef56ab12";

    private static Orcamento orcamento() {
        Orcamento orcamento = Orcamento.builder()
                .id(14L)
                .autonomo(Autonomo.builder()
                        .nomeNegocio("Clínica Bem Estar")
                        .documento("12345678909")
                        .cidade("Curitiba")
                        .estado("PR")
                        .build())
                .cliente(Cliente.builder().nome("Marina Souza").build())
                .versao(1)
                .status(StatusOrcamento.ACEITO)
                .validadeDias(7)
                .formaPagamento("Pix à vista")
                .observacoes("Atendimento em domicílio")
                .linkSlug(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .criadoEm(LocalDateTime.of(2026, 9, 1, 10, 0))
                .build();
        orcamento.adicionarItem(ItemOrcamento.builder()
                .descricao("Sessão de fisioterapia")
                .quantidade(10)
                .valorUnitario(new BigDecimal("80.00"))
                .desconto(new BigDecimal("50.00"))
                .build());
        return orcamento;
    }

    private static Aceite aceite(String comentario) {
        return Aceite.builder()
                .tipoDecisao(TipoDecisao.ACEITO)
                .nomeClienteInformado("Marina Souza")
                .cpfClienteInformado("52998224725")
                .ip("191.34.12.200")
                .timestamp(LocalDateTime.of(2026, 9, 2, 14, 30, 5))
                .hashDocumentoSha256(HASH)
                .comentario(comentario)
                .build();
    }

    private static String textoDoPdf(byte[] pdf) throws Exception {
        PdfReader leitor = new PdfReader(pdf);
        try {
            return new PdfTextExtractor(leitor).getTextFromPage(1);
        } finally {
            leitor.close();
        }
    }

    @Test
    void geraUmPdfValido() {
        byte[] pdf = ComprovantePdf.gerar(orcamento(), aceite(null));

        assertEquals("%PDF-", new String(pdf, 0, 5, StandardCharsets.US_ASCII));
        assertTrue(pdf.length > 1000);
    }

    @Test
    void trazOsDadosDoAceiteEOHash() throws Exception {
        String texto = textoDoPdf(ComprovantePdf.gerar(orcamento(), aceite("Pode começar na segunda")));

        assertTrue(texto.contains("Clínica Bem Estar"));
        assertTrue(texto.contains("AC-AB12CD34"));
        assertTrue(texto.contains("ACEITO DIGITALMENTE"));
        assertTrue(texto.contains("Marina Souza"));
        assertTrue(texto.contains("02/09/2026 às 14:30:05"));
        assertTrue(texto.contains(HASH));
        assertTrue(texto.contains("Pode começar na segunda"));
        assertTrue(texto.contains("Lei nº 14.063/2020"));
    }

    @Test
    void mascaraCpfEIpNoDocumento() throws Exception {
        String texto = textoDoPdf(ComprovantePdf.gerar(orcamento(), aceite(null)));

        assertTrue(texto.contains("191.34.xx.xx"));
        assertFalse(texto.contains("191.34.12.200"));
        assertFalse(texto.contains("52998224725"));
        assertFalse(texto.contains("529.982.247-25"));
    }

    @Test
    void aguentaTextoComCaracteresForaDoAlfabetoLatino() {
        Orcamento orcamento = orcamento();
        orcamento.getItens().get(0).setDescricao("Sessão 😀 日本");

        byte[] pdf = ComprovantePdf.gerar(orcamento, aceite(null));

        assertTrue(pdf.length > 1000);
    }
}
