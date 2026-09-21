package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Aceite;
import com.himapsystems.aceitaai.domain.Autonomo;
import com.himapsystems.aceitaai.domain.ItemOrcamento;
import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.util.Cpf;
import com.himapsystems.aceitaai.util.Ip;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Monta o PDF do comprovante de aceite. Usa as fontes padrao do PDF (Helvetica/Courier, WinAnsi), que cobrem
 * o portugues; caracteres fora disso (ex: emojis digitados pelo usuario) simplesmente nao aparecem.
 */
public final class ComprovantePdf {

    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color NAVY = new Color(0x16, 0x23, 0x3F);
    private static final Color TEXTO = new Color(0x1A, 0x1F, 0x2B);
    private static final Color MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDA = new Color(0xE2, 0xE5, 0xEB);
    private static final Color VERDE = new Color(0x1D, 0x9A, 0x5C);
    private static final Color VERDE_CLARO = new Color(0xE6, 0xF5, 0xEC);
    private static final Color FUNDO = new Color(0xF6, 0xF7, 0xF9);

    private ComprovantePdf() {
    }

    public static byte[] gerar(Orcamento orcamento, Aceite aceite) {
        String codigo = HashDocumento.codigoDoComprovante(aceite.getHashDocumentoSha256());
        Autonomo autonomo = orcamento.getAutonomo();

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 48, 48, 44, 44);

        try {
            PdfWriter.getInstance(documento, saida);
            documento.addTitle("Comprovante de aceite " + codigo);
            documento.addAuthor("Aceita.ai");
            documento.addCreator("Aceita.ai - Himap Systems");
            documento.open();

            documento.add(cabecalho(autonomo, codigo));
            documento.add(espaco(14));
            documento.add(selo(aceite));
            documento.add(espaco(16));

            documento.add(titulo("Dados do aceite"));
            documento.add(dadosDoAceite(aceite, codigo));

            if (aceite.getComentario() != null && !aceite.getComentario().isBlank()) {
                documento.add(espaco(10));
                documento.add(titulo("Mensagem do cliente"));
                documento.add(new Paragraph(aceite.getComentario(), fonte(10, Font.NORMAL, TEXTO)));
            }

            documento.add(espaco(16));
            documento.add(titulo("Orçamento aceito · nº " + orcamento.getId()));
            documento.add(itens(orcamento));
            documento.add(espaco(8));
            documento.add(condicoes(orcamento));

            documento.add(espaco(16));
            documento.add(impressaoDigital(aceite.getHashDocumentoSha256()));
            documento.add(espaco(14));
            documento.add(rodape());

            documento.close();
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível gerar o PDF do comprovante", e);
        }
        return saida.toByteArray();
    }

    private static PdfPTable cabecalho(Autonomo autonomo, String codigo) {
        PdfPTable tabela = tabela(new float[]{3f, 2f});

        Phrase negocio = new Phrase();
        negocio.add(new Chunk(autonomo.getNomeNegocio(), fonte(16, Font.BOLD, Color.WHITE)));
        String local = localDoNegocio(autonomo);
        if (local != null) {
            negocio.add(new Chunk("\n" + local, fonte(9, Font.NORMAL, new Color(0xC9, 0xD2, 0xE3))));
        }
        PdfPCell esquerda = new PdfPCell(negocio);
        estilizar(esquerda, NAVY, 16, Element.ALIGN_LEFT);
        tabela.addCell(esquerda);

        Phrase numero = new Phrase();
        numero.add(new Chunk("Comprovante nº", fonte(9, Font.NORMAL, new Color(0xC9, 0xD2, 0xE3))));
        numero.add(new Chunk("\n" + codigo, fonte(15, Font.BOLD, Color.WHITE)));
        PdfPCell direita = new PdfPCell(numero);
        estilizar(direita, NAVY, 16, Element.ALIGN_RIGHT);
        tabela.addCell(direita);

        return tabela;
    }

    private static PdfPTable selo(Aceite aceite) {
        PdfPTable tabela = tabela(new float[]{1f});

        Phrase texto = new Phrase();
        texto.add(new Chunk("ACEITO DIGITALMENTE", fonte(14, Font.BOLD, VERDE)));
        texto.add(new Chunk(
                "\nRegistrado em " + DATA_HORA.format(aceite.getTimestamp()),
                fonte(10, Font.NORMAL, VERDE)));

        PdfPCell celula = new PdfPCell(texto);
        estilizar(celula, VERDE_CLARO, 12, Element.ALIGN_CENTER);
        celula.setBorder(Rectangle.BOX);
        celula.setBorderColor(VERDE);
        celula.setBorderWidth(1f);
        tabela.addCell(celula);

        return tabela;
    }

    private static PdfPTable dadosDoAceite(Aceite aceite, String codigo) {
        PdfPTable tabela = tabela(new float[]{1.2f, 3f});
        linha(tabela, "Aceito por", aceite.getNomeClienteInformado());
        linha(tabela, "CPF", Cpf.mascarar(aceite.getCpfClienteInformado()));
        linha(tabela, "Data e hora", DATA_HORA.format(aceite.getTimestamp()));
        linha(tabela, "Endereço IP", Ip.mascarar(aceite.getIp()));
        linha(tabela, "Código do comprovante", codigo);
        return tabela;
    }

    private static PdfPTable itens(Orcamento orcamento) {
        PdfPTable tabela = tabela(new float[]{4f, 0.8f, 1.5f, 1.5f});
        cabecalhoDaTabela(tabela, "Item", Element.ALIGN_LEFT);
        cabecalhoDaTabela(tabela, "Qtd.", Element.ALIGN_CENTER);
        cabecalhoDaTabela(tabela, "Valor unit.", Element.ALIGN_RIGHT);
        cabecalhoDaTabela(tabela, "Total", Element.ALIGN_RIGHT);

        for (ItemOrcamento item : orcamento.getItens()) {
            Phrase descricao = new Phrase(item.getDescricao(), fonte(10, Font.NORMAL, TEXTO));
            if (item.getDesconto() != null && item.getDesconto().signum() > 0) {
                descricao.add(new Chunk(
                        "\ndesconto de " + moeda(item.getDesconto()), fonte(8, Font.NORMAL, MUTED)));
            }
            celulaDaTabela(tabela, descricao, Element.ALIGN_LEFT);
            celulaDaTabela(tabela, new Phrase(String.valueOf(item.getQuantidade()), fonte(10, Font.NORMAL, TEXTO)),
                    Element.ALIGN_CENTER);
            celulaDaTabela(tabela, new Phrase(moeda(item.getValorUnitario()), fonte(10, Font.NORMAL, TEXTO)),
                    Element.ALIGN_RIGHT);
            celulaDaTabela(tabela, new Phrase(moeda(OrcamentoCalculos.totalDoItem(item)), fonte(10, Font.BOLD, TEXTO)),
                    Element.ALIGN_RIGHT);
        }

        PdfPCell rotuloTotal = new PdfPCell(new Phrase("Total", fonte(11, Font.BOLD, TEXTO)));
        rotuloTotal.setColspan(3);
        estilizar(rotuloTotal, FUNDO, 8, Element.ALIGN_RIGHT);
        tabela.addCell(rotuloTotal);

        PdfPCell valorTotal = new PdfPCell(
                new Phrase(moeda(OrcamentoCalculos.totalDoOrcamento(orcamento)), fonte(12, Font.BOLD, NAVY)));
        estilizar(valorTotal, FUNDO, 8, Element.ALIGN_RIGHT);
        tabela.addCell(valorTotal);

        return tabela;
    }

    private static PdfPTable condicoes(Orcamento orcamento) {
        PdfPTable tabela = tabela(new float[]{1.2f, 3f});
        linha(tabela, "Preparado para", orcamento.getCliente().getNome());
        linha(tabela, "Enviado em", DATA.format(orcamento.getCriadoEm()));
        if (orcamento.getFormaPagamento() != null) {
            linha(tabela, "Pagamento", orcamento.getFormaPagamento());
        }
        if (orcamento.getObservacoes() != null) {
            linha(tabela, "Observações", orcamento.getObservacoes());
        }
        return tabela;
    }

    private static PdfPTable impressaoDigital(String hash) {
        PdfPTable tabela = tabela(new float[]{1f});

        Phrase texto = new Phrase();
        texto.add(new Chunk("Impressão digital do orçamento (SHA-256)\n", fonte(8, Font.BOLD, MUTED)));
        texto.add(new Chunk(hash, FontFactory.getFont(FontFactory.COURIER, 8.5f, Font.NORMAL, TEXTO)));

        PdfPCell celula = new PdfPCell(texto);
        estilizar(celula, FUNDO, 10, Element.ALIGN_LEFT);
        tabela.addCell(celula);
        return tabela;
    }

    private static Paragraph rodape() {
        Paragraph rodape = new Paragraph(
                "Este documento comprova a manifestação de aceite do cliente por meio eletrônico (assinatura eletrônica "
                        + "simples, Lei nº 14.063/2020). A impressão digital acima identifica o conteúdo exato do orçamento "
                        + "no momento do aceite: qualquer alteração posterior resultaria em um código diferente. "
                        + "Emitido por Aceita.ai · Himap Systems.",
                fonte(8, Font.NORMAL, MUTED));
        rodape.setAlignment(Element.ALIGN_JUSTIFIED);
        rodape.setLeading(11f);
        return rodape;
    }

    private static Paragraph titulo(String texto) {
        Paragraph paragrafo = new Paragraph(texto.toUpperCase(PT_BR), fonte(9, Font.BOLD, MUTED));
        paragrafo.setSpacingAfter(4f);
        return paragrafo;
    }

    private static Paragraph espaco(float altura) {
        Paragraph paragrafo = new Paragraph(" ", fonte(1, Font.NORMAL, Color.WHITE));
        paragrafo.setLeading(altura);
        return paragrafo;
    }

    private static PdfPTable tabela(float[] larguras) {
        PdfPTable tabela = new PdfPTable(larguras);
        tabela.setWidthPercentage(100);
        return tabela;
    }

    private static void linha(PdfPTable tabela, String rotulo, String valor) {
        PdfPCell esquerda = new PdfPCell(new Phrase(rotulo, fonte(10, Font.NORMAL, MUTED)));
        PdfPCell direita = new PdfPCell(new Phrase(valor, fonte(10, Font.BOLD, TEXTO)));
        for (PdfPCell celula : new PdfPCell[]{esquerda, direita}) {
            celula.setBorder(Rectangle.BOTTOM);
            celula.setBorderColor(BORDA);
            celula.setBorderWidth(0.6f);
            celula.setPaddingTop(6);
            celula.setPaddingBottom(6);
            celula.setPaddingLeft(0);
            celula.setPaddingRight(0);
            tabela.addCell(celula);
        }
    }

    private static void cabecalhoDaTabela(PdfPTable tabela, String texto, int alinhamento) {
        PdfPCell celula = new PdfPCell(new Phrase(texto, fonte(9, Font.BOLD, MUTED)));
        celula.setHorizontalAlignment(alinhamento);
        celula.setBorder(Rectangle.BOTTOM);
        celula.setBorderColor(NAVY);
        celula.setBorderWidth(1f);
        celula.setPaddingLeft(0);
        celula.setPaddingRight(0);
        celula.setPaddingBottom(6);
        tabela.addCell(celula);
    }

    private static void celulaDaTabela(PdfPTable tabela, Phrase conteudo, int alinhamento) {
        PdfPCell celula = new PdfPCell(conteudo);
        celula.setHorizontalAlignment(alinhamento);
        celula.setBorder(Rectangle.BOTTOM);
        celula.setBorderColor(BORDA);
        celula.setBorderWidth(0.6f);
        celula.setPaddingTop(6);
        celula.setPaddingBottom(6);
        celula.setPaddingLeft(0);
        celula.setPaddingRight(0);
        tabela.addCell(celula);
    }

    private static void estilizar(PdfPCell celula, Color fundo, float padding, int alinhamento) {
        celula.setBackgroundColor(fundo);
        celula.setBorder(Rectangle.NO_BORDER);
        celula.setPadding(padding);
        celula.setHorizontalAlignment(alinhamento);
    }

    private static Font fonte(float tamanho, int estilo, Color cor) {
        return FontFactory.getFont(FontFactory.HELVETICA, tamanho, estilo, cor);
    }

    private static String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor);
    }

    private static String localDoNegocio(Autonomo autonomo) {
        boolean temCidade = autonomo.getCidade() != null && !autonomo.getCidade().isBlank();
        boolean temEstado = autonomo.getEstado() != null && !autonomo.getEstado().isBlank();
        if (temCidade && temEstado) {
            return autonomo.getCidade().trim() + ", " + autonomo.getEstado().trim();
        }
        return temCidade ? autonomo.getCidade().trim() : null;
    }
}
