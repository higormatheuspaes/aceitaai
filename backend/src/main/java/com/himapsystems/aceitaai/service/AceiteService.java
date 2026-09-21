package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Aceite;
import com.himapsystems.aceitaai.domain.Autonomo;
import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.StatusOrcamento;
import com.himapsystems.aceitaai.domain.TipoDecisao;
import com.himapsystems.aceitaai.dto.DecisaoPublicaResponse;
import com.himapsystems.aceitaai.dto.DecisaoRequest;
import com.himapsystems.aceitaai.dto.ItemPublicoResponse;
import com.himapsystems.aceitaai.dto.OrcamentoPublicoResponse;
import com.himapsystems.aceitaai.exception.AcessoNegadoException;
import com.himapsystems.aceitaai.exception.ConflitoException;
import com.himapsystems.aceitaai.exception.RecursoNaoEncontradoException;
import com.himapsystems.aceitaai.repository.AceiteRepository;
import com.himapsystems.aceitaai.repository.OrcamentoRepository;
import com.himapsystems.aceitaai.security.OrigemRequisicao.Origem;
import com.himapsystems.aceitaai.util.Cpf;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AceiteService {

    private final OrcamentoRepository orcamentoRepository;
    private final AceiteRepository aceiteRepository;

    public AceiteService(OrcamentoRepository orcamentoRepository, AceiteRepository aceiteRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.aceiteRepository = aceiteRepository;
    }

    /**
     * visitanteId so vem preenchido quando a requisicao traz um token valido de um autonomo logado;
     * para o cliente final (sem conta) e sempre nulo.
     */
    @Transactional(readOnly = true)
    public OrcamentoPublicoResponse consultar(UUID slug, Long visitanteId) {
        Orcamento orcamento = orcamentoRepository.findFirstByLinkSlugOrderByVersaoDesc(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado"));

        Aceite decisao = aceiteRepository.findFirstByOrcamentoIdOrderByTimestampDescIdDesc(orcamento.getId()).orElse(null);
        Autonomo autonomo = orcamento.getAutonomo();

        return new OrcamentoPublicoResponse(
                autonomo.getNomeNegocio(),
                localDoNegocio(autonomo),
                orcamento.getCliente().getNome(),
                orcamento.getItens().stream()
                        .map(item -> new ItemPublicoResponse(
                                item.getDescricao(),
                                item.getQuantidade(),
                                item.getValorUnitario(),
                                item.getDesconto(),
                                OrcamentoCalculos.totalDoItem(item)))
                        .toList(),
                OrcamentoCalculos.totalDoOrcamento(orcamento),
                orcamento.getValidadeDias(),
                OrcamentoCalculos.validaAte(orcamento),
                orcamento.getFormaPagamento(),
                orcamento.getObservacoes(),
                orcamento.getCriadoEm(),
                orcamento.getVersao(),
                OrcamentoCalculos.statusEfetivo(orcamento),
                decisao == null ? null : toPublica(decisao),
                ehODono(orcamento, visitanteId)
        );
    }

    @Transactional
    public DecisaoPublicaResponse decidir(UUID slug, DecisaoRequest request, Origem origem, Long visitanteId) {
        Orcamento orcamento = orcamentoRepository.buscarUltimaVersaoParaAtualizar(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado"));

        if (ehODono(orcamento, visitanteId)) {
            throw new AcessoNegadoException("Quem criou o orçamento não pode respondê-lo. Envie o link ao cliente.");
        }
        if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
            throw new ConflitoException("Este orçamento já foi respondido");
        }
        if (OrcamentoCalculos.statusEfetivo(orcamento) == StatusOrcamento.EXPIRADO) {
            throw new ConflitoException("Este orçamento expirou");
        }

        String cpf = Cpf.normalizar(request.cpf());
        if (Cpf.mesmoNumero(cpf, orcamento.getAutonomo().getDocumento())) {
            throw new IllegalArgumentException(
                    "O CPF informado é o do responsável por este orçamento. Quem responde deve ser o cliente.");
        }

        String comentario = request.comentario() == null || request.comentario().isBlank()
                ? null
                : request.comentario().trim();
        if (request.tipo() == TipoDecisao.AJUSTE && comentario == null) {
            throw new IllegalArgumentException("Descreva o ajuste que você precisa");
        }

        Aceite aceite = aceiteRepository.save(Aceite.builder()
                .orcamento(orcamento)
                .tipoDecisao(request.tipo())
                .nomeClienteInformado(request.nome().trim())
                .cpfClienteInformado(cpf)
                .ip(origem.ip() == null ? "desconhecido" : origem.ip())
                .userAgent(origem.userAgent())
                .hashDocumentoSha256(HashDocumento.calcular(orcamento))
                .comentario(comentario)
                .build());

        orcamento.setStatus(statusDaDecisao(request.tipo()));
        return toPublica(aceite);
    }

    private boolean ehODono(Orcamento orcamento, Long visitanteId) {
        return visitanteId != null && visitanteId.equals(orcamento.getAutonomo().getId());
    }

    private StatusOrcamento statusDaDecisao(TipoDecisao tipo) {
        return switch (tipo) {
            case ACEITO -> StatusOrcamento.ACEITO;
            case RECUSADO -> StatusOrcamento.RECUSADO;
            case AJUSTE -> StatusOrcamento.AJUSTE;
        };
    }

    private DecisaoPublicaResponse toPublica(Aceite aceite) {
        return new DecisaoPublicaResponse(
                aceite.getTipoDecisao(),
                HashDocumento.codigoDoComprovante(aceite.getHashDocumentoSha256()),
                aceite.getNomeClienteInformado(),
                Cpf.mascarar(aceite.getCpfClienteInformado()),
                aceite.getTimestamp(),
                aceite.getComentario()
        );
    }

    private String localDoNegocio(Autonomo autonomo) {
        boolean temCidade = autonomo.getCidade() != null && !autonomo.getCidade().isBlank();
        boolean temEstado = autonomo.getEstado() != null && !autonomo.getEstado().isBlank();
        if (temCidade && temEstado) {
            return autonomo.getCidade().trim() + ", " + autonomo.getEstado().trim();
        }
        return temCidade ? autonomo.getCidade().trim() : null;
    }
}
