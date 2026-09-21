package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Aceite;
import com.himapsystems.aceitaai.domain.Cliente;
import com.himapsystems.aceitaai.domain.ItemOrcamento;
import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.StatusOrcamento;
import com.himapsystems.aceitaai.dto.DashboardResumoResponse;
import com.himapsystems.aceitaai.dto.DecisaoAutonomoResponse;
import com.himapsystems.aceitaai.dto.ItemOrcamentoRequest;
import com.himapsystems.aceitaai.dto.ItemOrcamentoResponse;
import com.himapsystems.aceitaai.dto.OrcamentoRequest;
import com.himapsystems.aceitaai.dto.OrcamentoResponse;
import com.himapsystems.aceitaai.dto.OrcamentoResumoResponse;
import com.himapsystems.aceitaai.dto.OrcamentoRevisaoRequest;
import com.himapsystems.aceitaai.dto.PageResponse;
import com.himapsystems.aceitaai.exception.ConflitoException;
import com.himapsystems.aceitaai.exception.RecursoNaoEncontradoException;
import com.himapsystems.aceitaai.repository.AceiteRepository;
import com.himapsystems.aceitaai.repository.AutonomoRepository;
import com.himapsystems.aceitaai.repository.ClienteRepository;
import com.himapsystems.aceitaai.repository.OrcamentoRepository;
import com.himapsystems.aceitaai.util.Cpf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final ClienteRepository clienteRepository;
    private final AutonomoRepository autonomoRepository;
    private final AceiteRepository aceiteRepository;

    public OrcamentoService(
            OrcamentoRepository orcamentoRepository,
            ClienteRepository clienteRepository,
            AutonomoRepository autonomoRepository,
            AceiteRepository aceiteRepository
    ) {
        this.orcamentoRepository = orcamentoRepository;
        this.clienteRepository = clienteRepository;
        this.autonomoRepository = autonomoRepository;
        this.aceiteRepository = aceiteRepository;
    }

    @Transactional
    public OrcamentoResponse criar(Long autonomoId, OrcamentoRequest request) {
        Cliente cliente = clienteRepository.findByIdAndAutonomoId(request.clienteId(), autonomoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));

        Orcamento orcamento = Orcamento.builder()
                .autonomo(autonomoRepository.getReferenceById(autonomoId))
                .cliente(cliente)
                .validadeDias(request.validadeDias())
                .formaPagamento(vazioParaNulo(request.formaPagamento()))
                .observacoes(vazioParaNulo(request.observacoes()))
                .build();

        adicionarItens(orcamento, request.itens());

        Orcamento salvo = orcamentoRepository.save(orcamento);
        return toResponse(salvo, null, salvo.getId());
    }

    /**
     * Cria a proxima versao de um orcamento em que o cliente pediu ajuste. A versao anterior fica como historico
     * (com o pedido do cliente) e o link continua o mesmo: quem abrir passa a ver a versao nova.
     */
    @Transactional
    public OrcamentoResponse revisar(Long autonomoId, Long id, OrcamentoRevisaoRequest request) {
        Orcamento base = orcamentoRepository.buscarParaRevisao(id, autonomoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado"));

        if (orcamentoRepository.existsByLinkSlugAndVersaoGreaterThan(base.getLinkSlug(), base.getVersao())) {
            throw new ConflitoException("Este orçamento já tem uma versão mais nova");
        }
        if (base.getStatus() != StatusOrcamento.AJUSTE) {
            throw new ConflitoException("Só é possível editar um orçamento em que o cliente pediu ajuste");
        }

        Orcamento nova = Orcamento.builder()
                .autonomo(base.getAutonomo())
                .cliente(base.getCliente())
                .orcamentoOriginal(base.getOrcamentoOriginal() != null ? base.getOrcamentoOriginal() : base)
                .versao(base.getVersao() + 1)
                .linkSlug(base.getLinkSlug())
                .validadeDias(request.validadeDias())
                .formaPagamento(vazioParaNulo(request.formaPagamento()))
                .observacoes(vazioParaNulo(request.observacoes()))
                .build();
        adicionarItens(nova, request.itens());

        Orcamento salva = orcamentoRepository.save(nova);
        return toResponse(salva, null, salva.getId());
    }

    private void adicionarItens(Orcamento orcamento, List<ItemOrcamentoRequest> itens) {
        for (ItemOrcamentoRequest item : itens) {
            BigDecimal desconto = item.desconto() != null ? item.desconto() : BigDecimal.ZERO;
            if (desconto.compareTo(OrcamentoCalculos.subtotalBruto(item.quantidade(), item.valorUnitario())) > 0) {
                throw new IllegalArgumentException(
                        "O desconto do item \"" + item.descricao().trim() + "\" nao pode ser maior que o valor do item");
            }

            orcamento.adicionarItem(ItemOrcamento.builder()
                    .descricao(item.descricao().trim())
                    .quantidade(item.quantidade())
                    .valorUnitario(item.valorUnitario())
                    .desconto(desconto)
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<OrcamentoResumoResponse> listar(Long autonomoId, int pagina, int tamanho) {
        Sort ordenacao = Sort.by(Sort.Direction.DESC, "criadoEm").and(Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(Math.max(0, pagina), Math.max(1, Math.min(tamanho, 100)), ordenacao);

        Page<Orcamento> resultado = orcamentoRepository.listarUltimasVersoes(autonomoId, pageable);

        return new PageResponse<>(
                resultado.getContent().stream().map(this::toResumo).toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public OrcamentoResponse buscar(Long autonomoId, Long id) {
        Orcamento orcamento = orcamentoRepository.findByIdAndAutonomoId(id, autonomoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado"));
        Aceite decisao = aceiteRepository.findFirstByOrcamentoIdOrderByTimestampDescIdDesc(orcamento.getId()).orElse(null);
        Long versaoAtualId = orcamentoRepository.findFirstByLinkSlugOrderByVersaoDesc(orcamento.getLinkSlug())
                .map(Orcamento::getId)
                .orElse(orcamento.getId());
        return toResponse(orcamento, decisao, versaoAtualId);
    }

    @Transactional(readOnly = true)
    public DashboardResumoResponse resumo(Long autonomoId) {
        LocalDateTime inicioDoMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        long enviados = orcamentoRepository.contarUltimasVersoesDesde(autonomoId, inicioDoMes);
        long aceitos = orcamentoRepository.contarUltimasVersoesPorStatusDesde(
                autonomoId, StatusOrcamento.ACEITO, inicioDoMes);
        Integer taxa = enviados == 0 ? null : (int) Math.round(aceitos * 100.0 / enviados);

        return new DashboardResumoResponse(enviados, aceitos, taxa);
    }

    private OrcamentoResponse toResponse(Orcamento orcamento, Aceite decisao, Long versaoAtualId) {
        var itens = orcamento.getItens().stream().map(this::toItemResponse).toList();

        return new OrcamentoResponse(
                orcamento.getId(),
                orcamento.getCliente().getId(),
                orcamento.getCliente().getNome(),
                orcamento.getCliente().getWhatsapp(),
                orcamento.getVersao(),
                OrcamentoCalculos.statusEfetivo(orcamento),
                orcamento.getValidadeDias(),
                OrcamentoCalculos.validaAte(orcamento),
                orcamento.getFormaPagamento(),
                orcamento.getObservacoes(),
                orcamento.getLinkSlug(),
                orcamento.getCriadoEm(),
                itens,
                OrcamentoCalculos.totalDoOrcamento(orcamento),
                decisao == null ? null : toDecisaoAutonomo(decisao),
                versaoAtualId
        );
    }

    private OrcamentoResumoResponse toResumo(Orcamento orcamento) {
        return new OrcamentoResumoResponse(
                orcamento.getId(),
                orcamento.getCliente().getId(),
                orcamento.getCliente().getNome(),
                descricaoResumida(orcamento),
                OrcamentoCalculos.totalDoOrcamento(orcamento),
                OrcamentoCalculos.statusEfetivo(orcamento),
                orcamento.getCriadoEm(),
                orcamento.getLinkSlug()
        );
    }

    private ItemOrcamentoResponse toItemResponse(ItemOrcamento item) {
        return new ItemOrcamentoResponse(
                item.getId(),
                item.getDescricao(),
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getDesconto(),
                OrcamentoCalculos.totalDoItem(item)
        );
    }

    private DecisaoAutonomoResponse toDecisaoAutonomo(Aceite aceite) {
        return new DecisaoAutonomoResponse(
                aceite.getTipoDecisao(),
                HashDocumento.codigoDoComprovante(aceite.getHashDocumentoSha256()),
                aceite.getNomeClienteInformado(),
                Cpf.mascarar(aceite.getCpfClienteInformado()),
                aceite.getIp(),
                aceite.getTimestamp(),
                aceite.getComentario(),
                aceite.getHashDocumentoSha256()
        );
    }

    private String descricaoResumida(Orcamento orcamento) {
        var itens = orcamento.getItens();
        if (itens.isEmpty()) {
            return "";
        }
        String primeiro = itens.get(0).getDescricao();
        return itens.size() == 1 ? primeiro : primeiro + " +" + (itens.size() - 1);
    }

    private String vazioParaNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
